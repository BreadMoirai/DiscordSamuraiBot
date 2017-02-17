package samurai.message.modifier;

import net.dv8tion.jda.core.entities.Message;

import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/14/2017
 */
public class MessageEdit {
    private long channelId, messageId;
    private Message content;
    private Consumer<Message> successConsumer;


    public MessageEdit(long channelId, long messageId, Message content) {
        this.channelId = channelId;
        this.messageId = messageId;
        this.content = content;
        successConsumer = null;
    }

    public Consumer<Message> getConsumer() {
        return successConsumer;
    }

    public MessageEdit setSuccessConsumer(Consumer<Message> consumer) {
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
}
