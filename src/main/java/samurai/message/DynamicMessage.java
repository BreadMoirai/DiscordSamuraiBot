package samurai.message;

import net.dv8tion.jda.core.entities.Message;
import samurai.action.Reaction;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * These messages can change base on user interaction through reactions
 *
 * @author TonTL
 * @since 4.0
 */
public abstract class DynamicMessage extends SamuraiMessage implements Callable<MessageEdit> {

    private static final int timeout = 15 * 60 * 1000;

    private long messageId;
    private boolean expired;
    private long lastActive;
    private Reaction action;
    private int stage;

    protected DynamicMessage() {
        expired = false;
        stage = 0;
    }

    public abstract boolean valid(Reaction messageAction);

    /**
     * what is called to create the initial message
     *
     * @param messageAction the command the user sent
     */
    public void execute(Reaction messageAction) {
        this.lastActive = messageAction.getTime();
    }

    /**
     * Defines Behavior for when User interacts with an emoji.
     *
     * @return A MessageEdit object that modifies the message
     */
    @Override
    public abstract MessageEdit call();

    public Consumer<Message> getConsumer() {
        return message -> this.messageId = Long.parseLong(message.getId());
    }


    //everything above should be overridden

    //setters and getters
    public long getMessageId() {
        return messageId;
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

    public boolean setValidReaction(Reaction action) {
        if (valid(action)) {
            this.action = action;
            return true;
        }
        return false;
    }

    protected Reaction getReaction() {
        return action;
    }

    public int getStage() {
        return stage;
    }

    public DynamicMessage setStage(int stage) {
        this.stage = stage;
        return this;
    }
}
