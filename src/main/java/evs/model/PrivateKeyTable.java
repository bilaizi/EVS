package evs.model;

import evs.util.RSA;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bilaizi on 17-5-11.
 */
public class PrivateKeyTable {
    private List<PrivateKey> privateKeyTable;

    public PrivateKeyTable() {
        try {
            this.privateKeyTable = new ArrayList<>(
                    Arrays.asList(
                            RSA.getPrivateKey("privatekey1.dat"),
                            RSA.getPrivateKey("privatekey2.dat"),
                            RSA.getPrivateKey("privatekey3.dat"),
                            RSA.getPrivateKey("privatekey4.dat"),
                            RSA.getPrivateKey("privatekey5.dat"),
                            RSA.getPrivateKey("privatekey6.dat"),
                            RSA.getPrivateKey("privatekey7.dat"),
                            RSA.getPrivateKey("privatekey8.dat"),
                            RSA.getPrivateKey("privatekey9.dat"),
                            RSA.getPrivateKey("privatekey10.dat"),
                            RSA.getPrivateKey("privatekey11.dat"),
                            RSA.getPrivateKey("privatekey12.dat"),
                            RSA.getPrivateKey("privatekey13.dat"),
                            RSA.getPrivateKey("privatekey14.dat"),
                            RSA.getPrivateKey("privatekey15.dat"),
                            RSA.getPrivateKey("privatekey16.dat"),
                            RSA.getPrivateKey("privatekey17.dat"),
                            RSA.getPrivateKey("privatekey18.dat"),
                            RSA.getPrivateKey("privatekey19.dat"),
                            RSA.getPrivateKey("privatekey20.dat"),
                            RSA.getPrivateKey("privatekey21.dat"),
                            RSA.getPrivateKey("privatekey22.dat"),
                            RSA.getPrivateKey("privatekey23.dat"),
                            RSA.getPrivateKey("privatekey24.dat"),
                            RSA.getPrivateKey("privatekey25.dat"),
                            RSA.getPrivateKey("privatekey26.dat"),
                            RSA.getPrivateKey("privatekey27.dat"),
                            RSA.getPrivateKey("privatekey28.dat"),
                            RSA.getPrivateKey("privatekey29.dat"),
                            RSA.getPrivateKey("privatekey30.dat"),
                            RSA.getPrivateKey("privatekey31.dat"),
                            RSA.getPrivateKey("privatekey32.dat"),
                            RSA.getPrivateKey("privatekey33.dat"),
                            RSA.getPrivateKey("privatekey34.dat"),
                            RSA.getPrivateKey("privatekey35.dat"),
                            RSA.getPrivateKey("privatekey36.dat"),
                            RSA.getPrivateKey("privatekey37.dat"),
                            RSA.getPrivateKey("privatekey38.dat"),
                            RSA.getPrivateKey("privatekey39.dat"),
                            RSA.getPrivateKey("privatekey40.dat"),
                            RSA.getPrivateKey("privatekey41.dat"),
                            RSA.getPrivateKey("privatekey42.dat"),
                            RSA.getPrivateKey("privatekey43.dat"),
                            RSA.getPrivateKey("privatekey44.dat"),
                            RSA.getPrivateKey("privatekey45.dat"),
                            RSA.getPrivateKey("privatekey46.dat"),
                            RSA.getPrivateKey("privatekey47.dat"),
                            RSA.getPrivateKey("privatekey48.dat"),
                            RSA.getPrivateKey("privatekey49.dat"),
                            RSA.getPrivateKey("privatekey50.dat"),
                            RSA.getPrivateKey("privatekey51.dat"),
                            RSA.getPrivateKey("privatekey52.dat"),
                            RSA.getPrivateKey("privatekey53.dat"),
                            RSA.getPrivateKey("privatekey54.dat"),
                            RSA.getPrivateKey("privatekey55.dat"),
                            RSA.getPrivateKey("privatekey56.dat"),
                            RSA.getPrivateKey("privatekey57.dat"),
                            RSA.getPrivateKey("privatekey58.dat"),
                            RSA.getPrivateKey("privatekey59.dat"),
                            RSA.getPrivateKey("privatekey60.dat"),
                            RSA.getPrivateKey("privatekey61.dat"),
                            RSA.getPrivateKey("privatekey62.dat"),
                            RSA.getPrivateKey("privatekey63.dat"),
                            RSA.getPrivateKey("privatekey64.dat"),
                            RSA.getPrivateKey("privatekey65.dat"),
                            RSA.getPrivateKey("privatekey66.dat"),
                            RSA.getPrivateKey("privatekey67.dat"),
                            RSA.getPrivateKey("privatekey68.dat"),
                            RSA.getPrivateKey("privatekey69.dat"),
                            RSA.getPrivateKey("privatekey70.dat"),
                            RSA.getPrivateKey("privatekey71.dat"),
                            RSA.getPrivateKey("privatekey72.dat"),
                            RSA.getPrivateKey("privatekey73.dat"),
                            RSA.getPrivateKey("privatekey74.dat"),
                            RSA.getPrivateKey("privatekey75.dat"),
                            RSA.getPrivateKey("privatekey76.dat"),
                            RSA.getPrivateKey("privatekey77.dat"),
                            RSA.getPrivateKey("privatekey78.dat"),
                            RSA.getPrivateKey("privatekey79.dat"),
                            RSA.getPrivateKey("privatekey80.dat"),
                            RSA.getPrivateKey("privatekey81.dat"),
                            RSA.getPrivateKey("privatekey82.dat"),
                            RSA.getPrivateKey("privatekey83.dat"),
                            RSA.getPrivateKey("privatekey84.dat"),
                            RSA.getPrivateKey("privatekey85.dat"),
                            RSA.getPrivateKey("privatekey86.dat"),
                            RSA.getPrivateKey("privatekey87.dat"),
                            RSA.getPrivateKey("privatekey88.dat"),
                            RSA.getPrivateKey("privatekey89.dat"),
                            RSA.getPrivateKey("privatekey90.dat"),
                            RSA.getPrivateKey("privatekey91.dat"),
                            RSA.getPrivateKey("privatekey92.dat"),
                            RSA.getPrivateKey("privatekey93.dat"),
                            RSA.getPrivateKey("privatekey94.dat"),
                            RSA.getPrivateKey("privatekey95.dat"),
                            RSA.getPrivateKey("privatekey96.dat"),
                            RSA.getPrivateKey("privatekey97.dat"),
                            RSA.getPrivateKey("privatekey98.dat"),
                            RSA.getPrivateKey("privatekey99.dat"),
                            RSA.getPrivateKey("privatekey100.dat")
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PrivateKey> getPrivateKeyTable() {
        return privateKeyTable;
    }
}

