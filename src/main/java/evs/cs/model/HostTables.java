package evs.cs.model;

import evs.model.*;
import evs.util.RSA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bilaizi on 17-3-20.
 */
public class HostTables {
    private List<HostInfo> hostInfoTables;

    public HostTables() throws Exception {
        this.hostInfoTables = new ArrayList<>(
                Arrays.asList(
                        new HostInfo("192.168.0.131", 6001, RSA.getPublicKey("publickey1.dat")),
                        new HostInfo("192.168.0.132", 6002, RSA.getPublicKey("publickey2.dat")),
                        new HostInfo("192.168.0.133", 6003, RSA.getPublicKey("publickey3.dat")),
                        new HostInfo("192.168.0.134", 6004, RSA.getPublicKey("publickey4.dat")),
                        new HostInfo("192.168.0.135", 6005, RSA.getPublicKey("publickey5.dat")),
                        new HostInfo("192.168.0.136", 6006, RSA.getPublicKey("publickey6.dat")),
                        new HostInfo("192.168.0.137", 6007, RSA.getPublicKey("publickey7.dat")),
                        new HostInfo("192.168.0.138", 6008, RSA.getPublicKey("publickey8.dat")),
                        new HostInfo("192.168.0.139", 6009, RSA.getPublicKey("publickey9.dat")),
                        new HostInfo("192.168.0.140", 6010, RSA.getPublicKey("publickey10.dat"))
                )
        );
    }

    public List<HostInfo> getHostInfoTables() {
        return hostInfoTables;
    }
}
