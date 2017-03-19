package samurai.osu;

import org.json.JSONObject;
import samurai.entities.SamuraiUser;
import samurai.util.OsuAPI;

import java.time.OffsetDateTime;
import java.util.LinkedList;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
public class OsuSession {
    private OffsetDateTime startTime;
    private String player;
    private long osuId;
    private int initialRankGlobal;
    private LinkedList<SamuraiUser> samuraiUsers;

    public OsuSession(SamuraiUser u) {
        startTime = OffsetDateTime.now();
        this.player = u.getOsuName();
        this.osuId = u.getOsuId();
        initialRankGlobal = u.getG_rank();
        final JSONObject userJSON = OsuAPI.getUserJSON(u.getOsuName());
        if (userJSON == null) {
            System.err.println("Failed to initialize session for player: " + player);
        } else
            initialRankGlobal = userJSON.getInt("pp_rank");
        samuraiUsers = new LinkedList<>();
        samuraiUsers.add(u);
    }

    public boolean match(SamuraiUser u) {
        return u.getOsuId() == osuId;
    }

    public void add(SamuraiUser u) {
        samuraiUsers.add(u);
    }


}
