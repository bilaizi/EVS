package evs.model;

/**
 * Created by bilaizi on 17-3-19.
 */
public class Data2 {
    private String serialNumber;
    private String ciperResponse;

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setCiperResponse(String ciperResponse) {
        this.ciperResponse = ciperResponse;
    }

    public String getCiperResponse() {
        return ciperResponse;
    }

    @Override
    public String toString() {
        return "Data2{" + "serialNumber='" + serialNumber + ", ciperResponse='" + ciperResponse + '\'' + '}';
    }
}
