package samurai.message;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import samurai.message.modifier.MessageEdit;
import samurai.message.modifier.Reaction;

import java.util.List;
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

    /**
     * This is the method that retrieves the message to be sent/updated to.
     * What is returned should be determined by what stage the dynamic message is in.
     *
     * @return A Message that will replace the current content
     */
    @Override
    public abstract Message getMessage();

    /**
     * This is method determines who gets to interact with the message.
     *
     * @param action This is the messageAction associated with this message. Consists of a reaction.
     * @return true if the Reaction is accepted. false otherwise
     */
    public abstract boolean valid(Reaction action);

    /**
     * When this method is called, valid() has also been called prior
     * This method executes the Reaction action
     * which should cause some change in the message
     * either indicated by a change in member fields or stage.
     *
     * @param action this is the reaction added by the user
     */
    protected abstract void execute(Reaction action);

    /**
     * This is used to edit the message that has been sent
     * Usually used to modify the reactions attached to the message
     *
     * @return a consumer for when the message has been sent
     */
    public abstract Consumer<Message> getConsumer();

    /**
     * Used with DynamicMessage#isExpired
     * ex. Connect Four has 4 possible forms so this method would return 3.
     *
     * @return the index of the last possible form of this object
     */
    protected abstract int getLastStage();



    /**
     * Defines Behavior for when User interacts with an emoji.
     * for default consumers, see getInitalConsumer() and getEditConsumer()
     * @return A MessageEdit object that modifies the message
     */
    @Override
    public MessageEdit call() {
        this.lastActive = getReaction().getTime();
        execute(getReaction());
        return new MessageEdit(getChannelId(), getMessageId(), getMessage()).setSuccessConsumer(getConsumer());
    }

    /**
     * will delete the most recent valid reaction added by a user
     * @return the consumer, use with getConsumer()
     */
    protected Consumer<Message> getEditConsumer() {
        return message -> {
            for (MessageReaction mr : message.getReactions())
                if (mr.getEmote().getName().equals(getReaction().getName()))
                    mr.removeReaction(getReaction().getUser()).queue();
        };
    }

    /**
     * adds the emojis and increments the stage
     * @param emoji unicode emojis
     * @return the consumer, use with getConsumer()
     */
    protected Consumer<Message> getInitialConsumer(List<String> emoji) {
        return message -> {
            emoji.forEach(reaction -> message.addReaction(reaction).complete());
            setStage(getStage()+1);
            message.editMessage(this.getMessage()).queue();
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
