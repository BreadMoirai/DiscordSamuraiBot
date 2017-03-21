package samurai.osu.tracker;

import samurai.entities.SamuraiGuild;
import samurai.entities.SamuraiUser;
import samurai.osu.entities.Score;
import samurai.osu.enums.GameMode;
import samurai.util.BotUtil;
import samurai.util.OsuAPI;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 5.x - 3/12/2017
 */
public class OsuTracker {

    private static final CopyOnWriteArrayList<OsuSession> tracking;
    private static final ScheduledExecutorService service;

    static {
        tracking = new CopyOnWriteArrayList<>();
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(OsuTracker::dotheThing, 3, 1, TimeUnit.MINUTES);
    }

    private static void dotheThing() {
        tracking.forEach(OsuSession::update);
    }

    private static List<Score> getRecentScore(SamuraiUser user) {
        return OsuAPI.getUserRecent(user.getOsuName(), user.getOsuId(), GameMode.OSU, 10);

    }

    public static void register(SamuraiUser u) {
        final Optional<OsuSession> session = tracking.stream().filter(osuSession -> osuSession.match(u)).findFirst();
        BotUtil.retrieveUser(u.getDiscordId()).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("I've got you in my sights.").queue());
    }

    public void unregister(List<SamuraiUser> u) {
        if (u.isEmpty()) return;
        u.forEach(tracking::remove);
        BotUtil.retrieveUser(u.get(0).getDiscordId()).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("I sense a weakness in you.").queue());
    }


    public static void register(SamuraiGuild samuraiGuild, SamuraiUser user) {

    }
}
