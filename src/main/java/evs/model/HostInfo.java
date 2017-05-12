package evs.model;

import java.security.PublicKey;

/**
 * Created by bilaizi on 17-3-15.
 */
public class HostInfo {
    private String host;
    private int port;
    private PublicKey publicKey;

    public HostInfo(String host, int port, PublicKey publicKey) {
        this.host = host;
        this.port = port;
        this.publicKey = publicKey;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
