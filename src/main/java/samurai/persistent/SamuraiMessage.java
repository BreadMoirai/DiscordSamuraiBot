package samurai.persistent;

import samurai.action.Reaction;

/**
 * @author TonTL
 * @since 4.0
 */
public abstract class SamuraiMessage {
    protected long messageId;

    abstract void execute(Reaction action);


    public long getMessageId() {
        return messageId;
    }

    public SamuraiMessage setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }
}
