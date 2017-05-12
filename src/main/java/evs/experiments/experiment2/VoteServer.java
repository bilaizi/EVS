package evs.experiments.experiment2;

import evs.model.HostInfo;
import evs.model.HostInfoTable;
import evs.model.VoteServerService;
import evs.util.RSA;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bilaizi on 17-3-20.
 */
public class VoteServer {
    public static void main(String[] args) throws Exception {
        PrivateKey privateKey = RSA.getPrivateKey("privatekey21.dat");
        int numberClients = 20;
        List<HostInfo> hostInfoList = new HostInfoTable().getHostInfoTable();
        List<HostInfo> hostInfoTable = new ArrayList<>(numberClients);
        for (int i = 0; i < numberClients; i++)
            hostInfoTable.add(i, hostInfoList.get(i));
        String voteServerHost = "192.168.0.151";
        VoteServerService voteServerService = new VoteServerService(privateKey, hostInfoTable, 8080, 100, voteServerHost);
        voteServerService.start();
        try {
            voteServerService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}