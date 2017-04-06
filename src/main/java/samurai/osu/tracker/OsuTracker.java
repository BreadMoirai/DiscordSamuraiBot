package samurai.osu.tracker;

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

    }

}
