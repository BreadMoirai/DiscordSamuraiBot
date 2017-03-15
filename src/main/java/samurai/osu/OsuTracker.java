package samurai.osu;

import samurai.data.SamuraiUser;
import samurai.osu.enums.GameMode;
import samurai.util.BotUtil;
import samurai.util.OsuAPI;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 5.x - 3/12/2017
 */
public class OsuTracker {

    private final CopyOnWriteArrayList<SamuraiUser> tracking;
    private final ScheduledExecutorService service;

    public OsuTracker() {
        tracking = new CopyOnWriteArrayList<>();
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::dotheThing, 10, 5, TimeUnit.MINUTES);
    }

    private void dotheThing() {
        tracking.forEach(user -> {
            getRecentScore(user);
        });
    }

    private List<Score> getRecentScore(SamuraiUser user) {
        return OsuAPI.getUserRecent(user.getOsuName(), user.getOsuId(), GameMode.OSU, 10);

    }

    public void register(List<SamuraiUser> u) {
        if (u.isEmpty()) return;
        u.forEach(tracking::add);
        BotUtil.retrieveUser(u.get(0).getDiscordId()).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("I've got you in my sights.").queue());
    }

    public void unregister(List<SamuraiUser> u) {
        if (u.isEmpty()) return;
        u.forEach(tracking::remove);
        BotUtil.retrieveUser(u.get(0).getDiscordId()).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("I sense a weakness in you.").queue());
    }


}
