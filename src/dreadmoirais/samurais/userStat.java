package dreadmoirais.samurais;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by TonTL on 1/15/2017.
 */
public class userStat {

    private String userID;
    private int timesFlamed;
    private int duelsWon;
    private int duelsFought;

    public userStat(String id) {
        userID = id;
    }

    public void writeData(DataOutputStream os) throws IOException {
        os.writeChars(userID);
        os.writeShort(timesFlamed);


    }
}
