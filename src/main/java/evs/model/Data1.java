package evs.model;

import java.io.Serializable;

/**
 * Created by bilaizi on 17-3-19.
 */
public class Data1 implements Serializable {
    private Sender sender;
    private String serialNumber;
    private String ciperData;
    private String ciperKey;

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setCiperData(String ciperData) {
        this.ciperData = ciperData;
    }

    public String getCiperData() {
        return ciperData;
    }

    public void setCiperKey(String ciperKey) {
        this.ciperKey = ciperKey;
    }

    public String getCiperKey() {
        return ciperKey;
    }

    @Override
    public String toString() {
        return "Data1{" + "sender=" + sender + ", serialNumber='" + serialNumber +
                ", ciperData='" + ciperData + ", ciperKey='" + ciperKey + '\'' + '}';
    }
}
