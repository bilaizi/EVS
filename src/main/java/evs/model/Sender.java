package evs.model;

import java.io.Serializable;

/**
 * Created by bilaizi on 17-5-4.
 */
public class Sender implements Serializable{
    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        return "Sender{" + "host='" + host + '}';
    }
}
