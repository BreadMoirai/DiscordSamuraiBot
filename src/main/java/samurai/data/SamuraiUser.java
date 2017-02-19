package samurai.data;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
public class SamuraiUser {
    private long discordId;
    private int osuId;
    private String osuName;


    public SamuraiUser(long discordId, int osuId, String osuName) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
    }

    public long getDiscordId() {
        return discordId;
    }

    public int getOsuId() {
        return osuId;
    }

    public String getOsuName() {
        return osuName;
    }
}
