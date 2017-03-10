package samurai;

import samurai.message.DynamicMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 3/9/2017
 */
public class MessageManager {
    private final ConcurrentHashMap<Long, DynamicMessage> messages = new ConcurrentHashMap<>();

    public void register(DynamicMessage message) {
        messages.put(message.getMessageId(), message);
    }

    public void unregister(DynamicMessage message) {
        messages.remove(message.getMessageId(), message);
    }
}