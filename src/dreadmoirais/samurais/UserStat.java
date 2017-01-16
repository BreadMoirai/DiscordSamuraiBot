package dreadmoirais.samurais;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by TonTL on 1/15/2017.
 */
public class UserStat {

    private String userID;
    private short timesFlamed;
    private short duelsWon;
    private short duelsFought;
    private boolean[] change;
    private int position;

    public UserStat(String id) {
        userID = id;
        timesFlamed = 0;
    }

    public void setPosition(int p) {
        position = p;
    }

    public void writeData(DataOutputStream os) throws IOException {
        os.writeChars(userID);
        os.writeShort(timesFlamed);

    }

    public void addFlame() {
        timesFlamed++;
    }

    @Override
    public String toString() {
        return "Times Flamed: " + timesFlamed;
    }

    public byte[] getDataBytes() {
        byte[] output = new byte[8+2];
        //write userID
        long l = Long.parseLong(userID);
        for (int i = 7; i >= 0; i--) {
            output[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        for (int i = 9; i >= 7; i--) {
            output[i] = (byte)(timesFlamed & 0xFF);
            timesFlamed >>= 8;
        }
        return output;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

}
