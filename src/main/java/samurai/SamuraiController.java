package samurai;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Channel;
import samurai.action.Action;
import samurai.action.Reaction;
import samurai.message.DynamicMessage;
import samurai.message.SamuraiMessage;

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
    private final ConcurrentHashMap<Long, DynamicMessage> messageMap;
    private JDA jda;
    private boolean running;

    SamuraiController(OperatingSystemMXBean operatingSystemMXBean) {
        commandPool = Executors.newCachedThreadPool();
        actionQueue = new LinkedBlockingQueue<>();
        messageMap = new ConcurrentHashMap<>();
        running = true;
//        Executors.newSingleThreadExecutor().execute(() -> {
//            while (running) {
//                takeCommand();
//            }
//        });
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::takeCommand, 1000, 0, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::clearInactive, 10, 5, TimeUnit.MINUTES);
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

    void execute(Reaction reaction) {
        if (messageMap.containsKey(reaction.getMessageId())) {
            DynamicMessage samuraiMessage = messageMap.get(reaction.getMessageId());
            if (samuraiMessage.valid(reaction)) {
                samuraiMessage.execute(reaction);
                if (samuraiMessage.isExpired()) {
                    messageMap.remove(samuraiMessage.getMessageId());
                }
            }
        }
    }

    private void takeCommand() {
        try {
            final SamuraiMessage samuraiMessage = actionQueue.take().get();
            jda.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue(message -> {
                if (samuraiMessage instanceof DynamicMessage) {
                    ((DynamicMessage) samuraiMessage).setMessageId(message.getId());
                    messageMap.put(((DynamicMessage) samuraiMessage).getMessageId(), (DynamicMessage) samuraiMessage);
                }
            });
        } catch (NullPointerException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void clearInactive() {
        messageMap.forEachValue(1000L, value -> {
            if (value.isExpired()) {
                messageMap.remove(value.getMessageId());
            }
        });
    }


    void setJDA(JDA jda) {
        this.jda = jda;
    }
}
