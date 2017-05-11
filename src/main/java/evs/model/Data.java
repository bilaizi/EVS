package evs.model;

import java.io.Serializable;

/**
 * Created by bilaizi on 17-3-19.
 */
public class Data implements Serializable {
    private String ciperData;
    private String ciperKey;
    private Boolean flag;//true 表示 vote,false 表示 voteResponse

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

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Boolean getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return "Data{" + "ciperData='" + ciperData + ", ciperKey='" + ciperKey + ", flag=" + flag + '}';
    }
}