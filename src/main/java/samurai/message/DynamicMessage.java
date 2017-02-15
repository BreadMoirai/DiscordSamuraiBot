package samurai.message;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
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
    private long lastActive;
    private Reaction action;
    private int stage;

    protected DynamicMessage() {
        stage = 0;
    }

    @Override
    public abstract Message getMessage();

    public abstract boolean valid(Reaction messageAction);

    protected abstract void execute();

    public abstract Consumer<Message> getConsumer();

    /**
     * Used with DynamicMessage#isExpired
     * ex. Connect Four has 4 possible forms so this method would return 3.
     *
     * @return the index of the last possible form of this object
     */
    protected abstract int getLastStage();

    //everything above should be overridden

    /**
     * Defines Behavior for when User interacts with an emoji.
     *
     * @return A MessageEdit object that modifies the message
     */
    @Override
    public MessageEdit call() {
        this.lastActive = getReaction().getTime();
        execute();
        return new MessageEdit(getChannelId(), getMessageId(), getMessage()).setSuccessConsumer(getConsumer());
    }

    protected Consumer<Message> getEditConsumer() {
        return message -> {
            for (MessageReaction mr : message.getReactions())
                if (mr.getEmote().getName().equals(getReaction().getName()))
                    mr.removeReaction(getReaction().getUser()).queue();
        };
    }

    public long getMessageId() {
        return messageId;
    }

    //setters and getters
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean isExpired() {
        return getStage() == getLastStage() || System.currentTimeMillis() - lastActive > timeout;
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

    protected int getStage() {
        return stage;
    }

    protected DynamicMessage setStage(int stage) {
        this.stage = stage;
        return this;
    }
}
