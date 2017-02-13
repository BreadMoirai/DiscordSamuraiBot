package samurai;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.entities.Channel;
import samurai.action.Action;
import samurai.action.Reaction;
import samurai.persistent.SamuraiMessage;

import java.util.concurrent.*;

/**
 * Controller for the SamuraiBot.
 *
 * @author TonTL
 * @since 4.0
 */
public class SamuraiController {
    private static Channel officialChannel;
    private final ExecutorService commandPool;
    private final BlockingQueue<Future<SamuraiMessage>> actionQueue;
    private final ConcurrentHashMap<Long, SamuraiMessage> messageMap;
    private boolean running;

    SamuraiController(OperatingSystemMXBean operatingSystemMXBean) {
        commandPool = Executors.newCachedThreadPool();
        actionQueue = new LinkedBlockingQueue<>();
        messageMap = new ConcurrentHashMap<>();
        running = true;
        Executors.newSingleThreadExecutor().execute(() -> {
            while (running) {
                takeCommand();
            }
        });
    }

    public static Channel getOfficialChannel() {
        return officialChannel;
    }

    static void setOfficialChannel(Channel officialChannel) {
        SamuraiController.officialChannel = officialChannel;
    }

    void execute(Action action) {
        actionQueue.offer(commandPool.submit(action));
    }

    void execute(Reaction action) {
        if (messageMap.containsKey(action.getMessageId())) {
            messageMap.get(action.getMessageId()).execute(action);
        }
    }

    private void takeCommand() {
        Future<SamuraiMessage> samuraiMessageFuture = null;
        SamuraiMessage samuraiMessage = null;
        try {
            samuraiMessageFuture = actionQueue.take();
            samuraiMessage = samuraiMessageFuture.get();
        } catch (NullPointerException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (samuraiMessage != null) messageMap.put(samuraiMessage.getMessageId(), samuraiMessage);
    }
}
