package evs.model;

import evs.util.RSA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bilaizi on 17-3-20.
 */
public class HostTable {
    private List<Host> hostTable;

    public HostTable() {
        try {
            this.hostTable = new ArrayList<>(
                    Arrays.asList(
                            new Host("192.168.0.131", 6001, RSA.getPublicKey("publickey1.dat")),
                            new Host("192.168.0.132", 6002, RSA.getPublicKey("publickey2.dat")),
                            new Host("192.168.0.133", 6003, RSA.getPublicKey("publickey3.dat")),
                            new Host("192.168.0.134", 6004, RSA.getPublicKey("publickey4.dat")),
                            new Host("192.168.0.135", 6005, RSA.getPublicKey("publickey5.dat")),
                            new Host("192.168.0.136", 6006, RSA.getPublicKey("publickey6.dat")),
                            new Host("192.168.0.137", 6007, RSA.getPublicKey("publickey7.dat")),
                            new Host("192.168.0.138", 6008, RSA.getPublicKey("publickey8.dat")),
                            new Host("192.168.0.139", 6009, RSA.getPublicKey("publickey9.dat")),
                            new Host("192.168.0.140", 6010, RSA.getPublicKey("publickey10.dat")),
                            new Host("192.168.0.141", 6011, RSA.getPublicKey("publickey11.dat")),
                            new Host("192.168.0.142", 6012, RSA.getPublicKey("publickey12.dat")),
                            new Host("192.168.0.143", 6013, RSA.getPublicKey("publickey13.dat")),
                            new Host("192.168.0.144", 6014, RSA.getPublicKey("publickey14.dat")),
                            new Host("192.168.0.145", 6015, RSA.getPublicKey("publickey15.dat")),
                            new Host("192.168.0.146", 6016, RSA.getPublicKey("publickey16.dat")),
                            new Host("192.168.0.147", 6017, RSA.getPublicKey("publickey17.dat")),
                            new Host("192.168.0.148", 6018, RSA.getPublicKey("publickey18.dat")),
                            new Host("192.168.0.149", 6019, RSA.getPublicKey("publickey19.dat")),
                            new Host("192.168.0.150", 6020, RSA.getPublicKey("publickey20.dat")),
                            new Host("192.168.0.151", 6021, RSA.getPublicKey("publickey21.dat")),
                            new Host("192.168.0.152", 6022, RSA.getPublicKey("publickey22.dat")),
                            new Host("192.168.0.153", 6023, RSA.getPublicKey("publickey23.dat")),
                            new Host("192.168.0.154", 6024, RSA.getPublicKey("publickey24.dat")),
                            new Host("192.168.0.155", 6025, RSA.getPublicKey("publickey25.dat")),
                            new Host("192.168.0.156", 6026, RSA.getPublicKey("publickey26.dat")),
                            new Host("192.168.0.157", 6027, RSA.getPublicKey("publickey27.dat")),
                            new Host("192.168.0.158", 6028, RSA.getPublicKey("publickey28.dat")),
                            new Host("192.168.0.159", 6029, RSA.getPublicKey("publickey29.dat")),
                            new Host("192.168.0.160", 6030, RSA.getPublicKey("publickey30.dat")),
                            new Host("192.168.0.161", 6031, RSA.getPublicKey("publickey31.dat")),
                            new Host("192.168.0.162", 6032, RSA.getPublicKey("publickey32.dat")),
                            new Host("192.168.0.163", 6033, RSA.getPublicKey("publickey33.dat")),
                            new Host("192.168.0.164", 6034, RSA.getPublicKey("publickey34.dat")),
                            new Host("192.168.0.165", 6035, RSA.getPublicKey("publickey35.dat")),
                            new Host("192.168.0.166", 6036, RSA.getPublicKey("publickey36.dat")),
                            new Host("192.168.0.167", 6037, RSA.getPublicKey("publickey37.dat")),
                            new Host("192.168.0.168", 6038, RSA.getPublicKey("publickey38.dat")),
                            new Host("192.168.0.169", 6039, RSA.getPublicKey("publickey39.dat")),
                            new Host("192.168.0.170", 6040, RSA.getPublicKey("publickey40.dat")),
                            new Host("192.168.0.171", 6041, RSA.getPublicKey("publickey41.dat")),
                            new Host("192.168.0.172", 6042, RSA.getPublicKey("publickey42.dat")),
                            new Host("192.168.0.173", 6043, RSA.getPublicKey("publickey43.dat")),
                            new Host("192.168.0.174", 6044, RSA.getPublicKey("publickey44.dat")),
                            new Host("192.168.0.175", 6045, RSA.getPublicKey("publickey45.dat")),
                            new Host("192.168.0.176", 6046, RSA.getPublicKey("publickey46.dat")),
                            new Host("192.168.0.177", 6047, RSA.getPublicKey("publickey47.dat")),
                            new Host("192.168.0.178", 6048, RSA.getPublicKey("publickey48.dat")),
                            new Host("192.168.0.179", 6049, RSA.getPublicKey("publickey49.dat")),
                            new Host("192.168.0.180", 6050, RSA.getPublicKey("publickey50.dat")),
                            new Host("192.168.0.181", 6051, RSA.getPublicKey("publickey51.dat")),
                            new Host("192.168.0.182", 6052, RSA.getPublicKey("publickey52.dat")),
                            new Host("192.168.0.183", 6053, RSA.getPublicKey("publickey53.dat")),
                            new Host("192.168.0.184", 6054, RSA.getPublicKey("publickey54.dat")),
                            new Host("192.168.0.185", 6055, RSA.getPublicKey("publickey55.dat")),
                            new Host("192.168.0.186", 6056, RSA.getPublicKey("publickey56.dat")),
                            new Host("192.168.0.187", 6057, RSA.getPublicKey("publickey57.dat")),
                            new Host("192.168.0.188", 6058, RSA.getPublicKey("publickey58.dat")),
                            new Host("192.168.0.189", 6059, RSA.getPublicKey("publickey59.dat")),
                            new Host("192.168.0.190", 6060, RSA.getPublicKey("publickey60.dat")),
                            new Host("192.168.0.191", 6061, RSA.getPublicKey("publickey61.dat")),
                            new Host("192.168.0.192", 6062, RSA.getPublicKey("publickey62.dat")),
                            new Host("192.168.0.193", 6063, RSA.getPublicKey("publickey63.dat")),
                            new Host("192.168.0.194", 6064, RSA.getPublicKey("publickey64.dat")),
                            new Host("192.168.0.195", 6065, RSA.getPublicKey("publickey65.dat")),
                            new Host("192.168.0.196", 6066, RSA.getPublicKey("publickey66.dat")),
                            new Host("192.168.0.197", 6067, RSA.getPublicKey("publickey67.dat")),
                            new Host("192.168.0.198", 6068, RSA.getPublicKey("publickey68.dat")),
                            new Host("192.168.0.199", 6069, RSA.getPublicKey("publickey69.dat")),
                            new Host("192.168.0.200", 6070, RSA.getPublicKey("publickey70.dat")),
                            new Host("192.168.0.201", 6071, RSA.getPublicKey("publickey71.dat")),
                            new Host("192.168.0.202", 6072, RSA.getPublicKey("publickey72.dat")),
                            new Host("192.168.0.203", 6073, RSA.getPublicKey("publickey73.dat")),
                            new Host("192.168.0.204", 6074, RSA.getPublicKey("publickey74.dat")),
                            new Host("192.168.0.205", 6075, RSA.getPublicKey("publickey75.dat")),
                            new Host("192.168.0.206", 6076, RSA.getPublicKey("publickey76.dat")),
                            new Host("192.168.0.207", 6077, RSA.getPublicKey("publickey77.dat")),
                            new Host("192.168.0.208", 6078, RSA.getPublicKey("publickey78.dat")),
                            new Host("192.168.0.209", 6079, RSA.getPublicKey("publickey79.dat")),
                            new Host("192.168.0.210", 6080, RSA.getPublicKey("publickey80.dat")),
                            new Host("192.168.0.211", 6081, RSA.getPublicKey("publickey81.dat")),
                            new Host("192.168.0.212", 6082, RSA.getPublicKey("publickey82.dat")),
                            new Host("192.168.0.213", 6083, RSA.getPublicKey("publickey83.dat")),
                            new Host("192.168.0.214", 6084, RSA.getPublicKey("publickey84.dat")),
                            new Host("192.168.0.215", 6085, RSA.getPublicKey("publickey85.dat")),
                            new Host("192.168.0.216", 6086, RSA.getPublicKey("publickey86.dat")),
                            new Host("192.168.0.217", 6087, RSA.getPublicKey("publickey87.dat")),
                            new Host("192.168.0.218", 6088, RSA.getPublicKey("publickey88.dat")),
                            new Host("192.168.0.219", 6089, RSA.getPublicKey("publickey89.dat")),
                            new Host("192.168.0.220", 6090, RSA.getPublicKey("publickey90.dat")),
                            new Host("192.168.0.221", 6091, RSA.getPublicKey("publickey91.dat")),
                            new Host("192.168.0.222", 6092, RSA.getPublicKey("publickey92.dat")),
                            new Host("192.168.0.223", 6093, RSA.getPublicKey("publickey93.dat")),
                            new Host("192.168.0.224", 6094, RSA.getPublicKey("publickey94.dat")),
                            new Host("192.168.0.225", 6095, RSA.getPublicKey("publickey95.dat")),
                            new Host("192.168.0.226", 6096, RSA.getPublicKey("publickey96.dat")),
                            new Host("192.168.0.227", 6097, RSA.getPublicKey("publickey97.dat")),
                            new Host("192.168.0.228", 6098, RSA.getPublicKey("publickey98.dat")),
                            new Host("192.168.0.229", 6099, RSA.getPublicKey("publickey99.dat")),
                            new Host("192.168.0.230", 6100, RSA.getPublicKey("publickey100.dat"))
                     )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Host> getHostTable() {
        return hostTable;
    }
}
