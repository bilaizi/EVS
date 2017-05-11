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
    private final List<Host> hostTable;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public VoteServerService(PrivateKey privateKey, List<Host> hostTable, int port, int backlog, String voteServerHost) throws IOException {
        this.privateKey = privateKey;
        this.hostTable = hostTable;
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
                String dsJsonString = dis.readUTF();
                dis.close();
                Data ds = parseObject(dsJsonString, Data.class);
                String ciperJsonString = ds.getCiperData();
                String ciperKey1 = ds.getCiperKey();
                String k1 = RSA.decrypt(ciperKey1, privateKey);
                String jsonString = AES.decrypt(ciperJsonString, k1);
                Data1 ds1 = parseObject(jsonString, Data1.class);
                Sender sender = ds1.getSender();
                String serialNumber = ds1.getSerialNumber();
                String ciperVote = ds1.getCiperVote();
                String ciperKey2 = ds1.getCiperKey();
                String k = RSA.decrypt(ciperKey2, privateKey);
                String voteJsonString = AES.decrypt(ciperVote, k);
                System.out.println("voteJsonString :" + voteJsonString);

                Host host = hostTable
                        .stream()
                        .filter(
                                s -> Objects.equals(s.getHost(), sender.getHost())
                        )
                        .findFirst()
                        .orElse(null);
                PublicKey lastHopPublicKey = host.getPublicKey();
                String response = "You's vote have received:" + voteJsonString;
                String ciperResponse = AES.encrypt(response, k);
                Data2 ds2 = new Data2();
                ds2.setSerialNumber(serialNumber);
                ds2.setCiperResponse(ciperResponse);
                jsonString = toJSONString(ds2);
                k1 = AES.generateKey();
                ciperJsonString = AES.encrypt(jsonString, k1);
                ciperKey2 = RSA.encrypt(k1, lastHopPublicKey);
                ds.setCiperData(ciperJsonString);
                ds.setCiperKey(ciperKey2);
                ds.setFlag(false);
                dsJsonString = toJSONString(ds);
                socket = new Socket(host.getHost(), host.getPort());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(dsJsonString);
                dos.close();
                socket.close();
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
