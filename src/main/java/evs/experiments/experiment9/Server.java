package evs.experiments.experiment9;

import evs.model.Host;
import evs.model.HostTable;
import evs.model.PrivateKeyTable;
import evs.model.ServerService;
import evs.util.RSA;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bilaizi on 17-5-10.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        int numberServers = 90;
        Host serverHost = new Host("192.168.0.221", 8080, RSA.getPublicKey("publickey91.dat"));
        List<PrivateKey> privateKeyList = new PrivateKeyTable().getPrivateKeyTable();
        List<PrivateKey> privateKeyTable = new ArrayList<>();
        List<Host> hostList = new HostTable().getHostTable();
        for (int i = 0; i < numberServers; i++)
            privateKeyTable.add(i, privateKeyList.get(i));
        List<Host> hostTable = new ArrayList<>(numberServers);
        for (int i = 0; i < numberServers; i++)
            hostTable.add(i, hostList.get(i));
        ServerService serverService = new ServerService(numberServers, serverHost, privateKeyTable, hostTable);
        serverService.start();
        try {
            serverService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}