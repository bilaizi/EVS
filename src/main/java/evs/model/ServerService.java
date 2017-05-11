package evs.model;

import evs.util.AES;
import evs.util.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by bilaizi on 17-5-10.
 */
public class ServerService extends Thread {
    private final int poolSize;
    private final ExecutorService pool;
    private final Host serverHost;
    private List<Host> hostTable;
    private List<PrivateKey> privateKeyTable;

    public ServerService(int poolSize, Host serverHost, List<PrivateKey> privateKeyTable, List<Host> hostTable) {
        this.poolSize = poolSize;
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.serverHost = serverHost;
        this.hostTable = hostTable;
        this.privateKeyTable = privateKeyTable;
    }

    @Override
    public void run() {
        String current;
        for (int i = 0; i < poolSize; i++) {
            current = hostTable.get(i).getHost();
            try {
                pool.execute(new NetworkService(6001 + i, 100, current, serverHost, privateKeyTable.get(i), i + 1, hostTable));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class NetworkService implements Runnable {
        private String currentHost;
        private final ServerSocket serverSocket;
        private final ExecutorService pool;
        private final Host serverHost;
        private final PrivateKey privateKey;
        private final Map<String, Stack<Sender>> routeMap = new ConcurrentHashMap<>();
        private final Map<String, String> responseMap = new ConcurrentHashMap<>();
        private final int serverId;
        private List<Host> hostTable;

        public NetworkService(int port, int backlog, String currentHost, Host serverHost, PrivateKey privateKey, int serverId, List<Host> hostTable) throws IOException {
            this.currentHost = currentHost;
            this.serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(currentHost));
            this.pool = Executors.newCachedThreadPool();
            this.serverHost = serverHost;
            this.privateKey = privateKey;
            this.serverId = serverId;
            this.hostTable = hostTable;
        }

        public void run() {
            try {
                for (; ; ) {
                    pool.execute(new WorkerThread(serverSocket.accept(), privateKey, serverHost, currentHost, routeMap, responseMap, serverId, hostTable));
                }
            } catch (IOException ex) {
                pool.shutdown();
            }
        }

        private class WorkerThread implements Runnable {
            private Socket socket;
            private final PrivateKey privateKey;
            private final Host serverHost;
            private final String currentHost;
            private final Map<String, Stack<Sender>> routeMap;
            private final Map<String, String> responseMap;
            private final int serverId;
            private List<Host> hostTable = new ArrayList<>();

            public WorkerThread(Socket socket, PrivateKey privateKey, Host serverHost, String currentHost, Map<String, Stack<Sender>> routeMap, Map<String, String> responseMap, int serverId, List<Host> hostTable) {
                this.socket = socket;
                this.privateKey = privateKey;
                this.serverHost = serverHost;
                this.currentHost = currentHost;
                this.routeMap = routeMap;
                this.responseMap = responseMap;
                this.serverId = serverId;
                this.hostTable.addAll(hostTable);
            }

            @Override
            public void run() {
                try {
                    if (((InetSocketAddress) socket.getRemoteSocketAddress()).getPort() == 5000 + serverId) {
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        String serialNumber = dis.readUTF();
                        String response = responseMap.getOrDefault(serialNumber, null);
                        while (response == null)
                            response = responseMap.getOrDefault(serialNumber, null);
                        responseMap.remove(serialNumber);
                        dos.writeUTF(response);
                        dos.close();
                        dis.close();
                    } else {
                        DataInputStream lastHopDIS = new DataInputStream(socket.getInputStream());
                        String lastDsJsonString = lastHopDIS.readUTF();
                        Data ds = parseObject(lastDsJsonString, Data.class);
                        String ciperJsonString = ds.getCiperData();
                        String ciperKey1 = ds.getCiperKey();
                        Boolean flag = ds.getFlag();
                        String secretKey1 = RSA.decrypt(ciperKey1, privateKey);
                        String jsonString = AES.decrypt(ciperJsonString, secretKey1);
                        Sender sender;
                        String serialNumber;
                        Host nextHop;
                        String nextHopHost;
                        int nextHopPort;
                        PublicKey nextHopPublicKey;
                        Socket nextHopSoket;
                        DataOutputStream nextHopDOS;
                        String nextDsJsonString;
                        Stack<Sender> senderStack;
                        if (flag) {
                            Data1 ds1 = parseObject(jsonString, Data1.class);
                            sender = ds1.getSender();
                            serialNumber = ds1.getSerialNumber();
                            if (routeMap.get(serialNumber) != null) {
                                senderStack = routeMap.get(serialNumber);
                                senderStack.push(sender);
                                routeMap.replace(serialNumber, senderStack);
                            } else {
                                senderStack = new Stack<>();
                                senderStack.push(sender);
                                routeMap.putIfAbsent(serialNumber, senderStack);
                            }
                            String lastHopHost = sender.getHost();
                            Sender sender1 = new Sender();
                            sender1.setHost(currentHost);
                            ds1.setSender(sender1);
                            jsonString = toJSONString(ds1);
                            String secretKey2 = AES.generateKey();
                            ciperJsonString = AES.encrypt(jsonString, secretKey2);
                            ds.setCiperData(ciperJsonString);
                            if (Math.random() > 0.5) {
                                nextHop = serverHost;
                            } else {
                                hostTable.removeIf(s -> Objects.equals(s.getHost(), lastHopHost));
                                hostTable.removeIf(s -> Objects.equals(s.getHost(), sender1.getHost()));
                                Random random = new Random();
                                int index = random.nextInt(hostTable.size());
                                nextHop = hostTable.get(index);
                            }
                            nextHopHost = nextHop.getHost();
                            nextHopPort = nextHop.getPort();
                            nextHopPublicKey = nextHop.getPublicKey();
                            String ciperKey2 = RSA.encrypt(secretKey2, nextHopPublicKey);
                            ds.setCiperKey(ciperKey2);
                            nextDsJsonString = toJSONString(ds);
                            nextHopSoket = new Socket(nextHopHost, nextHopPort);
                            nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                            nextHopDOS.writeUTF(nextDsJsonString);
                            nextHopSoket.close();
                        } else {
                            Data2 ds2 = parseObject(jsonString, Data2.class);
                            serialNumber = ds2.getSerialNumber();
                            senderStack = routeMap.get(serialNumber);
                            String response = ds2.getCiperResponse();
                            if (senderStack == null) {
                                responseMap.put(serialNumber, response);
                            } else {
                                sender = senderStack.pop();
                                if (!senderStack.empty()) {
                                    routeMap.replace(serialNumber, senderStack);
                                } else {
                                    routeMap.remove(serialNumber);
                                }
                                nextHop = hostTable.stream()
                                        .filter(s -> Objects.equals(s.getHost(), sender.getHost()))
                                        .findFirst()
                                        .get();
                                nextHopHost = nextHop.getHost();
                                nextHopPort = nextHop.getPort();
                                nextHopPublicKey = nextHop.getPublicKey();
                                String secretKey2 = AES.generateKey();
                                ciperJsonString = AES.encrypt(jsonString, secretKey2);
                                String ciperKey2 = RSA.encrypt(secretKey2, nextHopPublicKey);
                                ds.setCiperData(ciperJsonString);
                                ds.setCiperKey(ciperKey2);
                                nextDsJsonString = toJSONString(ds);
                                nextHopSoket = new Socket(nextHopHost, nextHopPort);
                                nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                                nextHopDOS.writeUTF(nextDsJsonString);
                                nextHopSoket.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("服务器 run 异常: " + e.getMessage());
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            socket = null;
                            System.out.println("服务端 finally 异常:" + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
