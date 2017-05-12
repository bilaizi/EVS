package evs.experiments.experiment8;

import evs.model.HostInfo;
import evs.model.HostInfoTable;
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
        int numberServers = 80;
        HostInfo voteServerHostInfo = new HostInfo("192.168.0.211", 8080, RSA.getPublicKey("publickey81.dat"));
        List<PrivateKey> privateKeyList = new PrivateKeyTable().getPrivateKeyTable();
        List<PrivateKey> privateKeyTable = new ArrayList<>();
        List<HostInfo> hostInfoList = new HostInfoTable().getHostInfoTable();
        for (int i = 0; i < numberServers; i++)
            privateKeyTable.add(i, privateKeyList.get(i));
        List<HostInfo> hostInfoTable = new ArrayList<>(numberServers);
        for (int i = 0; i < numberServers; i++)
            hostInfoTable.add(i, hostInfoList.get(i));
        ServerService serverService = new ServerService(numberServers, voteServerHostInfo, privateKeyTable, hostInfoTable);
        serverService.start();
        try {
            serverService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}