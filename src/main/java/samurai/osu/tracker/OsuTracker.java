package samurai.osu.tracker;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.entities.model.Player;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;

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
        service.scheduleAtFixedRate(OsuTracker::dotheThing, 1, 30, TimeUnit.SECONDS);
    }

    private static void dotheThing() {
        tracking.forEach(OsuSession::update);
    }

    public static boolean isTracking(long discordUserId) {
        LongPredicate idMatch = value -> discordUserId == value;
        return tracking.stream().map(OsuSession::getPlayer).mapToLong(Player::getDiscordId).anyMatch(idMatch);
    }

    public static Optional<OsuSession> retrieveSession(long discordUserId) {
        return tracking.stream().filter(osuSession -> osuSession.getPlayer().getDiscordId() == discordUserId).findAny();
    }


    public static boolean register(Player player, TextChannel channel) {
        final OsuSession osuSession = new OsuSession(player);
        osuSession.addChannel(channel);
        return tracking.add(osuSession);
    }

    public static void close() {
        tracking.clear();
        service.shutdown();
        System.out.println("Osu Tracker has shut down");
    }
}
