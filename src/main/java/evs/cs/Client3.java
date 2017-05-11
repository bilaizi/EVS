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

public class Client3 {
    public static Host serverHost;
    private static PrivateKey privateKey;

    static {
        try {
            serverHost = new Host("192.168.0.141", 8080, RSA.getPublicKey("publickey11.dat"));
            privateKey = RSA.getPrivateKey("privatekey3.dat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        List<Host> hostTables = new HostTables().getHostTables();
        hostTables.removeIf(s -> s.getPort() == 6003);
        Random random = new Random();
        int index = random.nextInt(hostTables.size());
        Host nextHop = hostTables.get(index);
        PublicKey publicKey = nextHop.getPublicKey();
        Vote vote = new Vote();
        String voteString = "00000003";
        vote.setVoteString(voteString);
        String voteJsonString = toJSONString(vote);
        System.out.println(voteJsonString);
        String secretKey1 = AES.generateKey();
        System.out.println(secretKey1);
        String ciperVoteJsonString = AES.encrypt(voteJsonString, secretKey1);
        String ciperKey1 = RSA.encrypt(secretKey1, serverHost.getPublicKey());
        Data1 ds1 = new Data1();
        Sender sender = new Sender();
        sender.setHost("192.168.0.133");
        String serialNumber = "3";
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

        socket = new Socket("192.168.0.133", 6003, InetAddress.getByName("192.168.0.133"), 5003);
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