package evs.experiments.experiment5;

import evs.model.ClientService;
import evs.model.HostInfo;
import evs.model.HostInfoTable;
import evs.util.RSA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bilaizi on 17-3-8.
 */

public class Client {
    public static void main(String[] args) throws Exception {
        HostInfo voteServerHostInfo = new HostInfo("192.168.0.181", 8080, RSA.getPublicKey("publickey51.dat"));
        int numberClients= 50;
        List<HostInfo> hostInfoList =new HostInfoTable().getHostInfoTable();
        List<HostInfo> hostInfoTable =new ArrayList<>(numberClients);
        for(int i=0;i<numberClients;i++)
            hostInfoTable.add(i, hostInfoList.get(i));
        ClientService clientService = new ClientService(voteServerHostInfo, hostInfoTable, numberClients);
        clientService.start();
        try {
            clientService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}