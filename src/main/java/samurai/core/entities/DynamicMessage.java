package samurai.core.entities;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import samurai.core.Bot;
import samurai.core.entities.modifier.DynamicMessageResponse;
import samurai.core.events.ReactionEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * These messages can change base on user interaction through reactions
 *
 * Initial method call order
 * <ol>
 *     <li>getMessage()</li>
 *     <li>getConsumer()</li>
 * </ol>
 *
 * Upon reaction
 * <ol>
 *     <li>valid()</li>
 *     <li>execute()</li>
 *     <li>getMessage()</li>
 *     <li>getConsumer()</li>
 *     <li>check isExpired()</li>
 * </ol>
 *
 * @author TonTL
 * @since 4.0
 */
public abstract class DynamicMessage extends SamuraiMessage implements Callable<DynamicMessageResponse> {

    private static final int TIMEOUT = 30;

    private Long messageId;
    private OffsetDateTime lastActive;
    private ReactionEvent action;
    private int stage;

    protected DynamicMessage() {
        stage = 0;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public Consumer<Message> getConsumer() {
        if (messageId == null) {
            Consumer<Message> consumer;
            if ((consumer = createConsumer()) != null) {
                return ((Consumer<Message>) message -> setMessageId(Long.parseLong(message.getId()))).andThen(consumer);
            } else
                return message -> setMessageId(Long.parseLong(message.getId()));
        } else
            return createConsumer();
    }

    /**
     * Defines Behavior for when User interacts with an emoji.
     * for default consumers, see getInitalConsumer() and newEditConsumer()
     *
     * @return A DynamicMessageResponse object that modifies the entities
     */
    @Override
    public DynamicMessageResponse call() {
        this.lastActive = getReaction().getTime();
        execute(getReaction());
        return new DynamicMessageResponse(getChannelId(), getMessageId(), getMessage(), isExpired()).setSuccessConsumer(getConsumer());
    }

    /**
     * This is method determines who gets to interact with the entities.
     *
     * @param action This is the messageAction associated with this entities. Consists of a reaction.
     * @return true if the Reaction is accepted. false otherwise
     */
    protected abstract boolean valid(ReactionEvent action);

    /**
     * When this method is called, valid() has also been called prior
     * This method executes the Reaction command
     * which should cause some change in the entities
     * either indicated by a change in member fields or stage.
     *
     * @param action this is the reaction added by the user
     */
    protected abstract void execute(ReactionEvent action);

    /**
     * This is used to edit the entities that has been sent
     * Usually used to modify the reactions attached to the entities
     *
     * @return a consumer for when the entities has been sent
     */
    public abstract Consumer<Message> createConsumer();

    /**
     * Used with DynamicMessage#isExpired
     * ex. Connect Four has 4 possible forms so this method would return 3.
     *
     * indicates at what point should the object stop receiving updates.
     *
     * @return the index of the last possible form of this object
     */
    protected abstract int getLastStage();




    /**
     * will delete the most recent valid reaction added by a user
     *
     * @return the consumer, use with createConsumer()
     */
    protected Consumer<Message> newEditConsumer() {
        return message -> {
            for (MessageReaction mr : message.getReactions())
                if (mr.getEmote().getName().equals(getReaction().getName()))
                    mr.removeReaction(Bot.getUser(getReaction().getUserId())).queue();
        };
    }

    /**
     * adds the emojis and increments the stage by 1
     *
     * @param emoji unicode emojis
     * @return the consumer, use with createConsumer()
     */
    protected Consumer<Message> newMenuConsumer(List<String> emoji) {
        return message -> {
            emoji.forEach(reaction -> message.addReaction(reaction).complete());
            setStage(getStage() + 1);
            message.editMessage(this.getMessage()).queue();
        };
    }

    public long getMessageId() {
        return messageId;
    }

    //setters and getters
    protected void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    protected void setExpired() {
        setStage(getLastStage());
    }

    public boolean isExpired() {
        return getStage() == getLastStage() || MINUTES.between(lastActive, OffsetDateTime.now()) > TIMEOUT;
    }

    public boolean setValidReaction(ReactionEvent action) {
        if (valid(action)) {
            this.action = action;
            return true;
        }
        return false;
    }

    protected ReactionEvent getReaction() {
        return action;
    }

    protected int getStage() {
        return stage;
    }

    protected void setStage(int stage) {
        this.stage = stage;
    }

    /**
     * increments stage and returns it
     *
     * @return the stage after incrementing by 1
     */
    protected int nextStage() {
        stage += 1;
        return stage;
    }


}
