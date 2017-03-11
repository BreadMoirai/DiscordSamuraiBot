package samurai.core;

import net.dv8tion.jda.core.JDA;
import samurai.core.entities.DynamicMessage;
import samurai.core.entities.SamuraiMessage;
import samurai.core.events.MessageEvent;
import samurai.core.events.listeners.MessageListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
class MessageManager implements MessageListener {

    private final JDA client;
    private final ConcurrentHashMap<Long, DynamicMessage> messageMap;

    MessageManager(JDA client) {
        this.client = client;
        messageMap = new ConcurrentHashMap<>();
    }


    void clearInactive() {
        messageMap.forEachValue(1000L, message -> {
            if (message.isExpired()) {
                messageMap.remove(message.getMessageId());
            }
        });
    }

    boolean isWatching(long l) {
        return messageMap.containsKey(l);
    }

    void submit(SamuraiMessage samuraiMessage) {
        client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId()))
                .sendMessage(samuraiMessage.getMessage())
                .queue(samuraiMessage.isPersistent()
                        ? samuraiMessage.getConsumer().andThen(message -> messageMap.put(Long.valueOf(message.getId()), (DynamicMessage) samuraiMessage))
                        : samuraiMessage.getConsumer());
    }

    @Override
    public void onMessageEvent(MessageEvent event) {

    }
}
