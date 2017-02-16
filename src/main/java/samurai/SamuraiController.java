package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import samurai.action.Action;
import samurai.action.Reaction;
import samurai.message.DynamicMessage;
import samurai.message.MessageEdit;
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
    private final BlockingQueue<Future<MessageEdit>> reactionQueue;
    private final ConcurrentHashMap<Long, DynamicMessage> messageMap;
    private JDA client;
    private boolean running;

    SamuraiController() {
        commandPool = Executors.newCachedThreadPool();
        actionQueue = new LinkedBlockingQueue<>();
        reactionQueue = new LinkedBlockingQueue<>();
        messageMap = new ConcurrentHashMap<>();
        running = true;
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::takeReaction, 1000, 1, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::takeAction, 1000, 1, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::clearInactive, 10, 5, TimeUnit.MINUTES);
    }

    public static Channel getOfficialChannel() {
        return officialChannel;
    }

    static void setOfficialChannel(Channel officialChannel) {
        SamuraiController.officialChannel = officialChannel;
    }

    void execute(Action action) {
        if (!actionQueue.offer(commandPool.submit(action)))
            new RejectedExecutionException("Could not add Action to Queue").printStackTrace();
    }

    void execute(Reaction reaction) {
        System.out.println("Reaction Found: " + reaction.getName());
        DynamicMessage samuraiMessage = messageMap.get(reaction.getMessageId());
        if (samuraiMessage.setValidReaction(reaction)) {
            System.out.println("Executing...");
            if (!reactionQueue.offer(commandPool.submit(samuraiMessage)))
                new RejectedExecutionException("Could not add Action to Queue").printStackTrace();
        }
    }

    private void takeReaction() {
        try {
            final MessageEdit edit = reactionQueue.take().get();
            client.getTextChannelById(String.valueOf(edit.getChannelId())).editMessageById(String.valueOf(edit.getMessageId()), edit.getContent()).queue(edit.getConsumer());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void takeAction() {
        try {
            final SamuraiMessage samuraiMessage = actionQueue.take().get();
            if (samuraiMessage instanceof DynamicMessage) {
                DynamicMessage dynamicMessage = (DynamicMessage) samuraiMessage;
                //I might make another message sent queue with .submit();
                Message message = client.getTextChannelById(String.valueOf(dynamicMessage.getChannelId())).sendMessage(dynamicMessage.getMessage()).complete();
                dynamicMessage.setMessageId(Long.parseLong(message.getId()));
                dynamicMessage.getConsumer().accept(message);
                messageMap.putIfAbsent(dynamicMessage.getMessageId(), dynamicMessage);
            } else {
                client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue();
            }
        } catch (InterruptedException | ExecutionException e) {
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

    boolean isWatching(long messageId) {
        return messageMap.containsKey(messageId);
    }

    void setJDA(JDA jda) {
        this.client = jda;
    }


}
