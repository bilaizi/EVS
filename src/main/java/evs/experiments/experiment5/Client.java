package evs.experiments.experiment5;

import evs.model.ClientService;
import evs.model.Host;
import evs.model.HostTable;
import evs.util.RSA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bilaizi on 17-3-8.
 */

public class Client {
    public static void main(String[] args) throws Exception {
        Host serverHost = new Host("192.168.0.181", 8080, RSA.getPublicKey("publickey51.dat"));
        int numberClients= 50;
        List<Host> hostList=new HostTable().getHostTable();
        List<Host> hostTable=new ArrayList<>(numberClients);
        for(int i=0;i<numberClients;i++)
            hostTable.add(i,hostList.get(i));
        ClientService clientService = new ClientService(serverHost, hostTable, numberClients);
        clientService.start();
        try {
            clientService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}