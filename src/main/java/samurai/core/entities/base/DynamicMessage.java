package samurai.core.entities.base;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.core.MessageManager;
import samurai.core.events.ReactionEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public abstract class DynamicMessage extends SamuraiMessage {


    private static final int TIMEOUT = 30;

    private long messageId;
    private OffsetDateTime lastActive;
    private MessageManager manager;

    protected static Consumer<Message> newMenu(List<String> emoji) {
        return message -> emoji.forEach(reaction -> message.addReaction(reaction).complete());
    }

    public boolean isExpired() {
        return MINUTES.between(lastActive, OffsetDateTime.now()) > TIMEOUT;
    }

    protected void unregister() {
        manager.unregister(this);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @Override
    public void onReady(MessageManager manager) {
        this.manager = manager;
        onReady();
    }

    /**
     * fired when the manager is ready to send messages
     */
    protected abstract void onReady();

    protected void submitNewMessage(Message message) {
        submitNewMessage(message, emptyConsumer -> {
        });
    }

    protected void submitNewMessage(Message message, Consumer<Message> success) {
        manager.submit(getChannelId(), message, success.andThen(result -> setMessageId(Long.valueOf(result.getId()))));
    }

    public void submitNewMessage(String message) {
        submitNewMessage(message, emptyConsumer -> {
        });
    }

    protected void submitNewMessage(String message, Consumer<Message> success) {
        Message newMessage = new MessageBuilder().append(message).build();
        manager.submit(getChannelId(), newMessage, success.andThen(result -> setMessageId(Long.valueOf(result.getId()))));
    }

    protected void submitNewMessageAndDeleteCurrent(String message) {
        Message newMessage = new MessageBuilder().append(message).build();
        manager.deleteMessage(getChannelId(), getMessageId());
        manager.submit(getChannelId(), newMessage, null);
    }

    protected void updateMessage(String message) {
        Message newMessage = new MessageBuilder().append(message).build();
        updateMessage(newMessage);
    }

    public void updateMessage(Message message) {
        updateMessage(message, null);
    }

    protected void updateMessage(Message message, Consumer<Message> success) {
        manager.editMessage(getChannelId(), messageId, message, success);
    }

    public void clearReactions() {
        manager.clearReactions(getChannelId(), getMessageId());
    }

    protected void removeReaction(ReactionEvent event) {
        removeReaction(event.getMessageId(), event.getUserId(), event.getName());
    }

    protected void removeReaction(long messageId, long userId, String name) {
        manager.removeReaction(getChannelId(), messageId, userId, name);
    }
}
