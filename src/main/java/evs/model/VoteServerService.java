package evs.model;

import evs.util.AES;
import evs.util.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by bilaizi on 17-5-11.
 */
public class VoteServerService extends Thread {
    private final PrivateKey privateKey;
    private final List<HostInfo> hostInfoTable;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public VoteServerService(
            PrivateKey privateKey,
            List<HostInfo> hostInfoTable,
            int port, int backlog,
            String voteServerHost
    ) throws IOException {
        this.privateKey = privateKey;
        this.hostInfoTable = hostInfoTable;
        this.serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(voteServerHost));
        this.pool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                pool.execute(new HandlerThread(serverSocket.accept(), privateKey));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;
        private final PrivateKey privateKey;

        HandlerThread(Socket socket, PrivateKey privateKey) {
            this.socket = socket;
            this.privateKey = privateKey;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                String jsonString = dis.readUTF();
                Data ds = parseObject(jsonString, Data.class);
                String ciperData = ds.getCiperData();
                String ciperKey = ds.getCiperKey();
                String k1 = RSA.decrypt(ciperKey, privateKey);
                jsonString = AES.decrypt(ciperData, k1);
                Data1 ds1 = parseObject(jsonString, Data1.class);
                Sender sender = ds1.getSender();

                System.out.println(sender.getHost());

                String serialNumber = ds1.getSerialNumber();
                ciperData = ds1.getCiperData();
                ciperKey = ds1.getCiperKey();
                String k = RSA.decrypt(ciperKey, privateKey);
                jsonString = AES.decrypt(ciperData, k);
                Vote vote = parseObject(jsonString, Vote.class);
                String voteString = vote.getVoteString();
                System.out.println("voteString :" + voteString);
                HostInfo hostInfo = hostInfoTable
                        .stream()
                        .filter(
                                s -> Objects.equals(s.getHost(), sender.getHost())
                        )
                        .findFirst()
                        .orElse(null);
                PublicKey publicKey = hostInfo.getPublicKey();
                String response = "I have received you's vote :[" + voteString+']';
                ciperData = AES.encrypt(response, k);
                Data2 ds2 = new Data2();
                ds2.setSerialNumber(serialNumber);
                ds2.setCiperData(ciperData);
                jsonString = toJSONString(ds2);
                k1 = AES.generateKey();
                ciperData = AES.encrypt(jsonString, k1);
                ciperKey = RSA.encrypt(k1, publicKey);
                ds.setCiperData(ciperData);
                ds.setCiperKey(ciperKey);
                ds.setFlag(false);
                jsonString = toJSONString(ds);
                dos.writeUTF(jsonString);
                dos.close();
                dis.close();
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
