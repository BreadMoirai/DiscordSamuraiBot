package samurai.entities;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
public class SamuraiUser {
    private final long discordId;
    private final int osuId;
    private final String osuName;
    private final int g_rank;
    private final int c_rank;
    private short l_rank;
    private long lastUpdated;


    SamuraiUser(long discordId, int osuId, String osuName, int g, int c, long lastUpdated) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
        this.g_rank = g;
        this.c_rank = c;
        this.lastUpdated = lastUpdated;
    }

    SamuraiUser(long discordId, int osuId, String osuName, int g, int c, short l, long lastUpdated) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
        this.g_rank = g;
        this.c_rank = c;
        this.l_rank = l;
        this.lastUpdated = lastUpdated;
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

    public void setL_rank(short l_rank) {
        this.l_rank = l_rank;
    }

    @Override
    public String toString() {
        return String.format("User{%n\tdiscordId=%d%n\tosuId=%d%n\tosuName=%s%nl_rank=%s%n}", discordId, osuId, osuName, l_rank);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
