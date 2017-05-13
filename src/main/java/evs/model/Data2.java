package evs.model;

/**
 * Created by bilaizi on 17-3-19.
 */
public class Data2 {
    private String serialNumber;
    private String ciperData;

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

    @Override
    public String toString() {
        return "Data2{" + "serialNumber='" + serialNumber + ", ciperData='" + ciperData + '\'' + '}';
    }
}
