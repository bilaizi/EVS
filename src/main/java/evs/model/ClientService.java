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
    private final HostInfo voteServerHostInfo;
    private List<HostInfo> hostInfoTable;
    private final int poolSize;
    private final ExecutorService pool;

    public ClientService(HostInfo voteServerHostInfo, List<HostInfo> hostInfoTable, int poolSize) {
        this.voteServerHostInfo = voteServerHostInfo;
        this.hostInfoTable = hostInfoTable;
        this.poolSize = poolSize;
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void run() {
        for (int i = 1; i <= poolSize; i++) {
            pool.execute(new WorkerThread(i, voteServerHostInfo, hostInfoTable));
        }
    }

    public class WorkerThread implements Runnable {
        private int workerId;
        private HostInfo voteServerHostInfo;
        private List<HostInfo> hostInfoTable = new ArrayList<>();

        public WorkerThread(int workerId, HostInfo voteServerHostInfo, List<HostInfo> hostInfoTable) {
            this.workerId = workerId;
            this.voteServerHostInfo = voteServerHostInfo;
            this.hostInfoTable.addAll(hostInfoTable);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            String currentHost = "192.168.0." + (130 + workerId);
            int currentPort = 6000 + workerId;
            hostInfoTable.removeIf(s -> s.getPort() == currentPort);
            Random random = new Random();
            int index = random.nextInt(hostInfoTable.size());
            HostInfo nextHop = hostInfoTable.get(index);
            PublicKey publicKey = nextHop.getPublicKey();
            Vote vote = new Vote();
            String voteString = "选票 " + Integer.toString(workerId);
            System.out.println(voteString);
            vote.setVoteString(voteString);
            String jsonString = toJSONString(vote);
            try {
                String k = AES.generateKey();
                String ciperData = AES.encrypt(jsonString, k);
                String ciperKey = RSA.encrypt(k, voteServerHostInfo.getPublicKey());
                Data1 ds1 = new Data1();
                Sender sender = new Sender();
                sender.setHost(currentHost);
                String serialNumber = Integer.toString(workerId);
                ds1.setSender(sender);
                ds1.setSerialNumber(serialNumber);
                ds1.setCiperData(ciperData);
                ds1.setCiperKey(ciperKey);
                jsonString = toJSONString(ds1);
                String k1 = AES.generateKey();
                ciperData = AES.encrypt(jsonString, k1);
                ciperKey = RSA.encrypt(k1, publicKey);
                Data ds = new Data();
                ds.setCiperData(ciperData);
                ds.setCiperKey(ciperKey);
                ds.setFlag(true);
                jsonString = toJSONString(ds);
                Socket socket = new Socket(nextHop.getHost(), nextHop.getPort());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(jsonString);
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
                String response = AES.decrypt(ciperResponse, k);
                long endTime = System.currentTimeMillis();
                Double elapsed = (endTime - startTime) / 1000d;
                System.out.println(response + " " + Double.toString(elapsed) + "senconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
