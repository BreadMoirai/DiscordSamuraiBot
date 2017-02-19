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
    private int g_rank;
    private int c_rank;
    private short l_rank;


    public SamuraiUser(long discordId, int osuId, String osuName) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
    }

    public SamuraiUser(long discordId, int osuId, String osuName, int g, int c, short l) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
        this.g_rank = g;
        this.c_rank = c;
        this.l_rank = l;
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

    public int getG_rank() {
        return g_rank;
    }

    public int getC_rank() {
        return c_rank;
    }

    public short getL_rank() {
        return l_rank;
    }
}
