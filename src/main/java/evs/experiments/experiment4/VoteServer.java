package evs.experiments.experiment4;

import evs.model.Host;
import evs.model.HostTable;
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
        PrivateKey privateKey = RSA.getPrivateKey("privatekey41.dat");
        int numberClients = 40;
        List<Host> hostList = new HostTable().getHostTable();
        List<Host> hostTable = new ArrayList<>(numberClients);
        for (int i = 0; i < numberClients; i++)
            hostTable.add(i, hostList.get(i));
        String voteServerHost = "192.168.0.171";
        VoteServerService voteServerService = new VoteServerService(privateKey, hostTable, 8080, 100, voteServerHost);
        voteServerService.start();
        try {
            voteServerService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}