package dreadmoirais.samurais;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;

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

    Member Member;

    private String userID;
    short timesFlamed;
    private short duelsWon;
    private short duelsFought;
    private boolean[] change;
    private int position;

    UserStat(String id, Member member) {
        userID = id;
        this.Member = member;
        timesFlamed = 0;
    }

    public UserStat setData(byte[] data) {
        byte[] flame = Arrays.copyOfRange(data,0,2);
        timesFlamed = (short) (((flame[0]&0xFF)<<8)|(flame[1]&0xFF));
        return this;
    }

    public MessageEmbed buildEmbed() {
        return new EmbedBuilder()
                .setAuthor(Member.getEffectiveName(), Member.getUser().getAvatarUrl(), null)
                .setColor(Member.getColor())
                .setFooter("SamuraiStats\u2122", "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg")
                .addField(new MessageEmbed.Field("Times Flamed", Short.toString(timesFlamed), true))
                .build();
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
        return "UserStat{" +
                "Member=" + Member +
                ", timesFlamed=" + timesFlamed +
                ", duelsWon=" + duelsWon +
                ", duelsFought=" + duelsFought +
                ", change=" + Arrays.toString(change) +
                '}';
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
