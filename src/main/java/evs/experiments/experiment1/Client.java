package evs.experiments.experiment1;

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
        HostInfo voteServerHostInfo = new HostInfo("192.168.0.141", 8080, RSA.getPublicKey("publickey11.dat"));
        int numberClients= 10;
        List<HostInfo> hostInfoList =new HostInfoTable().getHostInfoTable();
        List<HostInfo> hostInfoTable =new ArrayList<>(numberClients);
        for(int i=0;i<numberClients;i++)
            hostInfoTable.add(i, hostInfoList.get(i));
        ClientService clientService = new ClientService(voteServerHostInfo, hostInfoTable, 10);
        clientService.start();
        try {
            clientService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}