package evs.cs;

import evs.cs.model.HostTables;
import evs.model.*;
import evs.util.AES;
import evs.util.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Random;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by bilaizi on 17-3-8.
 */

public class Client1 {
    public static HostInfo serverHostInfo;
    private static PrivateKey privateKey;

    static {
        try {
            serverHostInfo = new HostInfo("192.168.0.141", 8080, RSA.getPublicKey("publickey11.dat"));
            privateKey = RSA.getPrivateKey("privatekey1.dat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        List<HostInfo> hostInfoTables = new HostTables().getHostInfoTables();
        hostInfoTables.removeIf(s -> s.getPort() == 6001);
        Random random = new Random();
        int index = random.nextInt(hostInfoTables.size());
        HostInfo nextHop = hostInfoTables.get(index);
        PublicKey publicKey = nextHop.getPublicKey();
        Vote vote = new Vote();
        String voteString = "00000001";
        vote.setVoteString(voteString);
        String voteJsonString = toJSONString(vote);
        System.out.println(voteJsonString);
        String secretKey1 = AES.generateKey();
        System.out.println(secretKey1);
        String ciperVoteJsonString = AES.encrypt(voteJsonString, secretKey1);
        String ciperKey1 = RSA.encrypt(secretKey1, serverHostInfo.getPublicKey());
        Data1 ds1 = new Data1();
        Sender sender = new Sender();
        sender.setHost("192.168.0.131");
        String serialNumber = "1";
        ds1.setSender(sender);
        ds1.setSerialNumber(serialNumber);
        ds1.setCiperVote(ciperVoteJsonString);
        ds1.setCiperKey(ciperKey1);
        String ds1JsonString = toJSONString(ds1);
        System.out.println(ds1JsonString);

        String secretKey2 = AES.generateKey();
        System.out.println(secretKey2);
        String ciperJsonString = AES.encrypt(ds1JsonString, secretKey2);
        String ciperKey2 = RSA.encrypt(secretKey2, publicKey);

        Data ds = new Data();
        ds.setCiperData(ciperJsonString);
        ds.setCiperKey(ciperKey2);
        ds.setFlag(true);
        String dsJsonString = toJSONString(ds);
        System.out.println(dsJsonString);
        Socket socket = new Socket(nextHop.getHost(), nextHop.getPort());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(dsJsonString);
        socket = new Socket("192.168.0.131", 6001, InetAddress.getByName("192.168.0.131"), 5001);
        dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        dos.writeUTF(serialNumber);
        String ciperResponse = dis.readUTF();
        String response = AES.decrypt(ciperResponse, secretKey1);
        System.out.println(response);
        long endTime = System.currentTimeMillis();
        float elapsed = (endTime - startTime) / 1000F;
        System.out.println(Float.toString(elapsed) + "senconds");
    }
}