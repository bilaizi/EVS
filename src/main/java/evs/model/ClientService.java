package evs.model;

import evs.util.AES;
import evs.util.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by bilaizi on 17-5-10.
 */
public class ClientService extends Thread {
    private final Host serverHost;
    private List<Host> hostTable;
    private final int poolSize;
    private final ExecutorService pool;

    public ClientService(Host serverHost, List<Host> hostTable, int poolSize) {
        this.serverHost = serverHost;
        this.hostTable = hostTable;
        this.poolSize = poolSize;
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void run() {
        for (int i = 1; i <= poolSize; i++) {
            pool.execute(new WorkerThread(i, serverHost, hostTable));
        }
    }

    public class WorkerThread implements Runnable {
        private int workerId;
        private Host serverHost;
        private List<Host> hostTable = new ArrayList<>();

        public WorkerThread(int workerId, Host serverHost, List<Host> hostTable) {
            this.workerId = workerId;
            this.serverHost = serverHost;
            this.hostTable.addAll(hostTable);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            String currentHost = "192.168.0." + (130 + workerId);
            int currentPort = 6000 + workerId;
            hostTable.removeIf(s -> s.getPort() == currentPort);
            Random random = new Random();
            int index = random.nextInt(hostTable.size());
            Host nextHop = hostTable.get(index);
            PublicKey publicKey = nextHop.getPublicKey();
            Vote vote = new Vote();
            String voteString = Integer.toString(workerId);
            vote.setVoteString(voteString);
            String voteJsonString = toJSONString(vote);
            System.out.println(voteJsonString);
            try {
                String secretKey1 = AES.generateKey();
                String ciperVoteJsonString = AES.encrypt(voteJsonString, secretKey1);
                String ciperKey1 = RSA.encrypt(secretKey1, serverHost.getPublicKey());
                Data1 ds1 = new Data1();
                Sender sender = new Sender();
                sender.setHost(currentHost);
                String serialNumber = Integer.toString(workerId);
                ds1.setSender(sender);
                ds1.setSerialNumber(serialNumber);
                ds1.setCiperVote(ciperVoteJsonString);
                ds1.setCiperKey(ciperKey1);
                String ds1JsonString = toJSONString(ds1);
                String secretKey2 = AES.generateKey();
                String ciperJsonString = AES.encrypt(ds1JsonString, secretKey2);
                String ciperKey2 = RSA.encrypt(secretKey2, publicKey);
                Data ds = new Data();
                ds.setCiperData(ciperJsonString);
                ds.setCiperKey(ciperKey2);
                ds.setFlag(true);
                String dsJsonString = toJSONString(ds);
                Socket socket = new Socket(nextHop.getHost(), nextHop.getPort());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(dsJsonString);
                dos.close();
                socket.close();
                socket = new Socket(currentHost, currentPort, InetAddress.getByName(currentHost), 5000 + workerId);
                dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                dos.writeUTF(serialNumber);
                String ciperResponse = dis.readUTF();
                dis.close();
                dos.close();
                socket.close();
                String response = AES.decrypt(ciperResponse, secretKey1);
                long endTime = System.currentTimeMillis();
                Double elapsed = (endTime - startTime) / 1000d;
                System.out.println(response + " " + Double.toString(elapsed) + "senconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
