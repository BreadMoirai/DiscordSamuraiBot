package samurai.data;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
public class SamuraiUser {
    private long discordId;
    private int osuId;
    private String name;

    public SamuraiUser(long discordId, int osuId, String name) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.name = name;
    }
}
