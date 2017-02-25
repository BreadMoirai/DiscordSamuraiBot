package samurai.message.modifier;

import net.dv8tion.jda.core.entities.Message;

import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/14/2017
 */
public class DynamicMessageResponse {
    private final long channelId, messageId;
    private final Message content;
    private boolean dead;
    private Consumer<Message> successConsumer;


    public DynamicMessageResponse(long channelId, long messageId, Message content, boolean remove) {
        this.channelId = channelId;
        this.messageId = messageId;
        this.content = content;
        this.dead = remove;
        successConsumer = null;
    }

    public Consumer<Message> getConsumer() {
        return successConsumer;
    }

    public DynamicMessageResponse setSuccessConsumer(Consumer<Message> consumer) {
        successConsumer = consumer;
        return this;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public Message getContent() {
        return content;
    }

    public boolean isDead() {
        return dead;
    }
}
