package evs.model;

import java.io.Serializable;

/**
 * Created by bilaizi on 17-5-4.
 */
public class Vote implements Serializable {
    private String voteString;

    public void setVoteString(String voteString) {
        this.voteString = voteString;
    }

    public String getVoteString() {
        return voteString;
    }

    @Override
    public String toString() {
        return "Vote{" + "voteString='" + voteString + '}';
    }
}
