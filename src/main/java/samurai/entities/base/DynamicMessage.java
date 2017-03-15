package samurai.entities.base;

import net.dv8tion.jda.core.entities.Message;
import samurai.core.MessageManager;

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

    protected void register() {
        manager.register(this);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void onReady(MessageManager manager) {
        this.manager = manager;
        lastActive = OffsetDateTime.now();
        onReady(getChannel());
    }

}
