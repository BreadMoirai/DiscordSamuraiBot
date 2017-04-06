package samurai.messages.base;

import net.dv8tion.jda.core.entities.Message;
import samurai.messages.MessageManager;

import java.util.function.Consumer;

/**
 * @author TonTL
 * @since 5.0
 */
public abstract class SamuraiMessage {


    private long channelId;
    private long messageId;

    /**
     * This is the method that retrieves the messages to be sent/updated to.
     *
     * @return the messages that will be sent/replace
     */

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public void onReady(MessageManager messageManager) {
        messageManager.getClient().getTextChannelById(String.valueOf(channelId)).sendMessage(initialize()).queue(setId().andThen(this::onReady));
    }

    /**
     * provide the initial message to send.
     * @return your message
     */
    protected abstract Message initialize();

    /**
     * This method is used when the message is sent to the channel with initialize()
     * @param message that was sent with initialize()
     */
    protected abstract void onReady(Message message);

    private Consumer<Message> setId() {
        return message -> this.setMessageId(message.getId());
    }

    public long getMessageId() {
        return messageId;
    }

    private void setMessageId(String messageId) {
        this.messageId = Long.parseLong(messageId);
    }
}
