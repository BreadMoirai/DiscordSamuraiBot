package dreadmoirais.samurais;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by TonTL on 1/15/2017.
 * Stats
 *
 */
class UserStat {

    static final int BYTE_LENGTH = 10;

    String name;

    private String userID;
    private short timesFlamed;
    private short duelsWon;
    private short duelsFought;
    private boolean[] change;
    private int position;

    UserStat(String id, String name) {
        userID = id;
        this.name = name;
        timesFlamed = 0;
    }

    public UserStat setData(byte[] data) {
        byte[] flame = Arrays.copyOfRange(data,0,2);
        timesFlamed = (short) (((flame[0]&0xFF)<<8)|(flame[1]&0xFF));
        return this;
    }

    void setPosition(int p) {
        position = p;
    }

    public void writeData(DataOutputStream os) throws IOException {
        os.writeChars(userID);
        os.writeShort(timesFlamed);

    }

    void addFlame() {
        timesFlamed++;
    }

    @Override
    public String toString() {
        return "**" + name + "**\nTimes Flamed: " + timesFlamed;
    }

    byte[] getDataBytes() {
        byte[] output = new byte[BYTE_LENGTH];
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

}
