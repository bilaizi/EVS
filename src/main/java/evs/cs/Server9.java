package evs.cs;

import evs.cs.model.HostTables;
import evs.model.*;
import evs.util.AES;
import evs.util.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by bilaizi on 17-3-8.
 */
public class Server9 {
    public static Host serverHost;
    private static PrivateKey privateKey;
    private final Map<String, Stack<Sender>> routeMap = new ConcurrentHashMap<>();
    private final Map<String, String> responseMap = new ConcurrentHashMap<>();

    static {
        try {
            serverHost = new Host("192.168.0.141", 8080, RSA.getPublicKey("publickey11.dat"));
            privateKey = RSA.getPrivateKey("privatekey9.dat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) {
        Server9 server = new Server9();
        server.init();
    }

    private void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(6009, 100, InetAddress.getByName("192.168.0.139"));
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;

        HandlerThread(Socket socket) {
            this.socket = socket;
            new Thread(this).start();
        }

        public void run() {
            try {
                if (((InetSocketAddress) socket.getRemoteSocketAddress()).getPort() == 5009) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    String serialNumber = dis.readUTF();
                    String response = responseMap.getOrDefault(serialNumber, null);
                    while (response == null)
                        response = responseMap.getOrDefault(serialNumber, null);
                    responseMap.remove(serialNumber);
                    dos.writeUTF(response);
                } else {
                    System.out.println(9);
                    DataInputStream lastHopDIS = new DataInputStream(socket.getInputStream());
                    String lastDsJsonString = lastHopDIS.readUTF();
                    System.out.println("lastDsJsonString :" + lastDsJsonString);

                    Data ds = parseObject(lastDsJsonString, Data.class);
                    String ciperJsonString = ds.getCiperData();
                    String ciperKey1 = ds.getCiperKey();
                    Boolean flag = ds.getFlag();
                    String secretKey1 = RSA.decrypt(ciperKey1, getPrivateKey());
                    System.out.println(secretKey1);
                    String jsonString = AES.decrypt(ciperJsonString, secretKey1);
                    System.out.println("jsonString :" + jsonString);
                    List<Host> hostTables = new HostTables().getHostTables();
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
                            System.out.println(sender);
                            senderStack.push(sender);
                            System.out.println(senderStack.elements().nextElement());
                            routeMap.putIfAbsent(serialNumber, senderStack);
                            System.out.println(routeMap.get(serialNumber).peek());
                        }
                        String lastHopHost = sender.getHost();
                        System.out.println("lastHopHost :" + lastHopHost);
                        Sender sender9 = new Sender();
                        sender9.setHost("192.168.0.139");
                        ds1.setSender(sender9);
                        jsonString = toJSONString(ds1);
                        String secretKey2 = AES.generateKey();
                        ciperJsonString = AES.encrypt(jsonString, secretKey2);
                        ds.setCiperData(ciperJsonString);

                        if (Math.random() > 0.5) {
                            nextHop = serverHost;
                        } else {
                            hostTables.removeIf(s -> Objects.equals(s.getHost(), lastHopHost));
                            hostTables.removeIf(s -> Objects.equals(s.getHost(), sender9.getHost()));
                            Random random = new Random();
                            int index = random.nextInt(hostTables.size());
                            nextHop = hostTables.get(index);
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

                    } else {
                        Data2 ds2 = parseObject(jsonString, Data2.class);
                        serialNumber = ds2.getSerialNumber();
                        System.out.println(serialNumber);
                        senderStack = routeMap.get(serialNumber);
                        String response = ds2.getCiperResponse();
                        if (senderStack == null) {
                            responseMap.put(serialNumber, response);
                        } else {
                            sender = senderStack.pop();
                            System.out.println(sender);
                            if (!senderStack.empty()) {
                                routeMap.replace(serialNumber, senderStack);
                            } else {
                                routeMap.remove(serialNumber);
                            }
                            nextHop = hostTables.stream()
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
                            System.out.println(nextDsJsonString);
                            nextHopSoket = new Socket(nextHopHost, nextHopPort);
                            nextHopDOS = new DataOutputStream(nextHopSoket.getOutputStream());
                            nextHopDOS.writeUTF(nextDsJsonString);
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
