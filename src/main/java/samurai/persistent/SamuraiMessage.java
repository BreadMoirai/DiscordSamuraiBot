package samurai.persistent;

import samurai.action.Reaction;

/**
 * @author TonTL
 * @since 4.0
 */
public abstract class SamuraiMessage {

    private static final int timeout = 15 * 60 * 1000;

    private long messageId;
    private boolean expired;
    private long lastActive;

    public abstract boolean valid(Reaction reaction);

    public void execute(Reaction reaction) {
        this.lastActive = reaction.getTime();
    }

    public long getMessageId() {
        return messageId;
    }

    public SamuraiMessage setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public SamuraiMessage setMessageId(String id) {
        this.messageId = Long.parseLong(id);
        return this;
    }

    public boolean isExpired() {
        return expired || System.currentTimeMillis() - lastActive > timeout;
    }

    protected void setExpired() {
        expired = true;
    }

    protected SamuraiMessage setLastActive(long lastActive) {
        this.lastActive = lastActive;
        return this;
    }
}
