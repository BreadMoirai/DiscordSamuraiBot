package samurai.message;

/**
 * @author TonTL
 * @since 4.0
 */
public abstract class SamuraiMessage {
    protected long messageId;


    public long getMessageId() {
        return messageId;
    }

    public SamuraiMessage setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }
}
