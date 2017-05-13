package evs.cs;

import evs.cs.model.HostTables;
import evs.model.*;
import evs.util.AES;
import evs.util.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Objects;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by bilaizi on 17-3-20.
 */
public class ServiceServer {
    private static List<HostInfo> hostInfoTables;
    private static PrivateKey privateKey;

    static {
        try {
            hostInfoTables = new HostTables().getHostInfoTables();
            privateKey = RSA.getPrivateKey("privatekey11.dat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) {
        ServiceServer server = new ServiceServer();
        server.init();
    }

    private void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080, 100, InetAddress.getByName("192.168.0.141"));

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
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String dsJsonString = dis.readUTF();
                System.out.println("dsJsonString :" + dsJsonString);

                Data ds = parseObject(dsJsonString, Data.class);
                String ciperJsonString = ds.getCiperData();
                String ciperKey1 = ds.getCiperKey();
                String secretKey1 = RSA.decrypt(ciperKey1, getPrivateKey());
                System.out.println(secretKey1);
                String jsonString = AES.decrypt(ciperJsonString, secretKey1);
                System.out.println("jsonString :" + jsonString);
                Data1 ds1 = parseObject(jsonString, Data1.class);
                Sender sender = ds1.getSender();
                System.out.println(sender);
                String serialNumber = ds1.getSerialNumber();
                String ciperVote = ds1.getCiperData();
                String ciperKey2 = ds1.getCiperKey();
                String secretKey2 = RSA.decrypt(ciperKey2, getPrivateKey());
                System.out.println(secretKey1);
                String voteJsonString = AES.decrypt(ciperVote, secretKey2);
                System.out.println("voteJsonString :" + voteJsonString);

                HostInfo hostInfo = hostInfoTables
                        .stream()
                        .filter(
                                s -> Objects.equals(s.getHost(), sender.getHost())
                        )
                        .findFirst()
                        .orElse(null);
                PublicKey lastHopPublicKey = hostInfo.getPublicKey();
                System.out.println("lastHopPublicKey :" + lastHopPublicKey);
                String response = "You's vote have received:" + voteJsonString;
                String ciperResponse = AES.encrypt(response, secretKey2);
                Data2 ds2 = new Data2();
                ds2.setSerialNumber(serialNumber);
                ds2.setCiperData(ciperResponse);
                jsonString = toJSONString(ds2);

                secretKey2 = AES.generateKey();
                System.out.println(secretKey2);
                ciperJsonString = AES.encrypt(jsonString, secretKey2);
                ciperKey2 = RSA.encrypt(secretKey2, lastHopPublicKey);

                ds.setCiperData(ciperJsonString);
                ds.setCiperKey(ciperKey2);
                ds.setFlag(false);
                dsJsonString = toJSONString(ds);
                System.out.println(dsJsonString);
                socket = new Socket(hostInfo.getHost(), hostInfo.getPort());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(dsJsonString);
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