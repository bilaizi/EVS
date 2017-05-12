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
    private final HostInfo voteServerHostInfo;
    private List<HostInfo> hostInfoTable;
    private List<PrivateKey> privateKeyTable;

    public ServerService(int poolSize, HostInfo voteServerHostInfo, List<PrivateKey> privateKeyTable, List<HostInfo> hostInfoTable) {
        this.poolSize = poolSize;
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.voteServerHostInfo = voteServerHostInfo;
        this.hostInfoTable = hostInfoTable;
        this.privateKeyTable = privateKeyTable;
    }

    @Override
    public void run() {
        String serverHost;
        for (int i = 0; i < poolSize; i++) {
            serverHost = hostInfoTable.get(i).getHost();
            try {
                pool.execute(new NetworkService(6001 + i, 100, serverHost, voteServerHostInfo, privateKeyTable.get(i), i + 1, hostInfoTable));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class NetworkService implements Runnable {
        private String serverHost;
        private final ServerSocket serverSocket;
        private final ExecutorService pool;
        private final HostInfo voteServerHostInfo;
        private final PrivateKey privateKey;
        private final Map<String, Stack<Sender>> routeMap = new ConcurrentHashMap<>();
        private final Map<String, String> responseMap = new ConcurrentHashMap<>();
        private final int serverId;
        private List<HostInfo> hostInfoTable;

        public NetworkService(
                int port,
                int backlog,
                String serverHost,
                HostInfo voteServerHostInfo,
                PrivateKey privateKey,
                int serverId,
                List<HostInfo> hostInfoTable
        ) throws IOException {
            this.serverHost = serverHost;
            this.serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(serverHost));
            this.pool = Executors.newCachedThreadPool();
            this.voteServerHostInfo = voteServerHostInfo;
            this.privateKey = privateKey;
            this.serverId = serverId;
            this.hostInfoTable = hostInfoTable;
        }

        public void run() {
            try {
                for (; ; ) {
                    pool.execute(new WorkerThread(serverSocket.accept(), privateKey, voteServerHostInfo, serverHost, routeMap, responseMap, serverId, hostInfoTable));
                }
            } catch (IOException ex) {
                pool.shutdown();
            }
        }

        private class WorkerThread implements Runnable {
            private Socket socket;
            private final PrivateKey privateKey;
            private final HostInfo voteServerHostInfo;
            private final String serverHost;
            private final Map<String, Stack<Sender>> routeMap;
            private final Map<String, String> responseMap;
            private final int serverId;
            private List<HostInfo> hostInfoTable = new ArrayList<>();

            public WorkerThread(
                    Socket socket,
                    PrivateKey privateKey,
                    HostInfo voteServerHostInfo,
                    String serverHost,
                    Map<String, Stack<Sender>> routeMap,
                    Map<String, String> responseMap,
                    int serverId,
                    List<HostInfo> hostInfoTable
            ) {
                this.socket = socket;
                this.privateKey = privateKey;
                this.voteServerHostInfo = voteServerHostInfo;
                this.serverHost = serverHost;
                this.routeMap = routeMap;
                this.responseMap = responseMap;
                this.serverId = serverId;
                this.hostInfoTable.addAll(hostInfoTable);
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
                        String dsJsonString = lastHopDIS.readUTF();
                        lastHopDIS.close();
                        Data ds = parseObject(dsJsonString, Data.class);
                        String ciperJsonString = ds.getCiperData();
                        String ciperKey = ds.getCiperKey();
                        Boolean flag = ds.getFlag();
                        String k1 = RSA.decrypt(ciperKey, privateKey);
                        String jsonString = AES.decrypt(ciperJsonString, k1);
                        if (flag) {
                            Data1 ds1 = parseObject(jsonString, Data1.class);
                            Sender sender = ds1.getSender();
                            String serialNumber = ds1.getSerialNumber();
                            if (Math.random() > 0.5) {
                                Sender sender1 = new Sender();
                                sender1.setHost(serverHost);
                                ds1.setSender(sender1);
                                jsonString = toJSONString(ds1);
                                k1 = AES.generateKey();
                                ciperJsonString = AES.encrypt(jsonString, k1);
                                ds.setCiperData(ciperJsonString);
                                HostInfo hostInfo = voteServerHostInfo;
                                String host = hostInfo.getHost();
                                int port = hostInfo.getPort();
                                PublicKey publicKey = hostInfo.getPublicKey();
                                String ciperKey2 = RSA.encrypt(k1, publicKey);
                                ds.setCiperKey(ciperKey2);
                                dsJsonString = toJSONString(ds);
                                Socket nextHopSoket = new Socket(host, port);
                                DataOutputStream nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                                DataInputStream nextHopDIS = new DataInputStream(nextHopSoket.getInputStream());
                                nextHopDOS.writeUTF(dsJsonString);
                                dsJsonString = nextHopDIS.readUTF();
                                nextHopDIS.close();
                                nextHopDOS.close();
                                nextHopSoket.close();
                                ds = parseObject(dsJsonString, Data.class);
                                ciperJsonString = ds.getCiperData();
                                ciperKey = ds.getCiperKey();
                                k1 = RSA.decrypt(ciperKey, privateKey);
                                jsonString = AES.decrypt(ciperJsonString, k1);
                                hostInfo = hostInfoTable
                                        .stream()
                                        .filter(
                                                s -> Objects.equals(s.getHost(), sender.getHost())
                                        )
                                        .findFirst()
                                        .orElse(null);
                                System.out.println(sender.getHost());
                                host = hostInfo.getHost();
                                port = hostInfo.getPort();
                                publicKey = hostInfo.getPublicKey();
                                k1 = AES.generateKey();
                                ciperJsonString = AES.encrypt(jsonString, k1);
                                ciperKey2 = RSA.encrypt(k1, publicKey);
                                ds.setCiperData(ciperJsonString);
                                ds.setCiperKey(ciperKey2);
                                dsJsonString = toJSONString(ds);
                                System.out.println(dsJsonString);
                                nextHopSoket = new Socket(host, port);
                                nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                                nextHopDOS.writeUTF(dsJsonString);
                                nextHopDOS.close();
                            } else {
                                Stack<Sender> senderStack;
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
                                sender1.setHost(serverHost);
                                ds1.setSender(sender1);
                                jsonString = toJSONString(ds1);
                                k1 = AES.generateKey();
                                ciperJsonString = AES.encrypt(jsonString, k1);
                                ds.setCiperData(ciperJsonString);
                                hostInfoTable.removeIf(s -> Objects.equals(s.getHost(), lastHopHost));
                                hostInfoTable.removeIf(s -> Objects.equals(s.getHost(), serverHost));
                                Random random = new Random();
                                int index = random.nextInt(hostInfoTable.size());
                                HostInfo nextHop = hostInfoTable.get(index);
                                String host = nextHop.getHost();
                                int port = nextHop.getPort();
                                PublicKey publicKey = nextHop.getPublicKey();
                                ciperKey = RSA.encrypt(k1, publicKey);
                                ds.setCiperKey(ciperKey);
                                dsJsonString = toJSONString(ds);
                                Socket nextHopSoket = new Socket(host, port);
                                DataOutputStream nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                                nextHopDOS.writeUTF(dsJsonString);
                                nextHopDOS.close();
                                nextHopSoket.close();
                            }
                        } else {
                            Data2 ds2 = parseObject(jsonString, Data2.class);
                            String serialNumber = ds2.getSerialNumber();
                            Stack<Sender> senderStack = routeMap.get(serialNumber);
                            String response = ds2.getCiperResponse();
                            if (senderStack == null) {
                                responseMap.put(serialNumber, response);
                            } else {
                                Sender sender = senderStack.pop();
                                if (!senderStack.empty()) {
                                    routeMap.replace(serialNumber, senderStack);
                                } else {
                                    routeMap.remove(serialNumber);
                                }
                                HostInfo nextHop = hostInfoTable.stream()
                                        .filter(s -> Objects.equals(s.getHost(), sender.getHost()))
                                        .findFirst()
                                        .get();
                                String host = nextHop.getHost();
                                int port = nextHop.getPort();
                                PublicKey publicKey = nextHop.getPublicKey();
                                k1 = AES.generateKey();
                                ciperJsonString = AES.encrypt(jsonString, k1);
                                ciperKey = RSA.encrypt(k1, publicKey);
                                ds.setCiperData(ciperJsonString);
                                ds.setCiperKey(ciperKey);
                                dsJsonString = toJSONString(ds);
                                Socket nextHopSoket = new Socket(host, port);
                                DataOutputStream nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                                nextHopDOS.writeUTF(dsJsonString);
                                nextHopDOS.close();
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
