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

    private OffsetDateTime lastActive;
    private MessageManager manager;

    protected static Consumer<Message> newMenu(List<String> emoji) {
        return message -> emoji.forEach(reaction -> message.addReaction(reaction).complete());
    }

    public boolean isExpired() {
        return MINUTES.between(lastActive, OffsetDateTime.now()) > TIMEOUT;
    }

    /**
     * Unregisters this object with the messageManager.
     * This object will stop receiving any events and should fall to garbage collection
     */
    protected void unregister() {
        manager.unregister(this);
    }

    /**
     * Registers this object with the messageManager.
     * Doing so allows the object to receive events for listener interface <? extends SamuraiListener>
     */
    protected void register() {
        manager.register(this);
    }

    @Override
    public void onReady(MessageManager messageManager) {
        setActive();
        this.manager = messageManager;
        super.onReady(messageManager);
        register();
    }

    /**
     * use this to refresh the timeout of your message. By default, dynamic message are automaticall unregistered at 30 minutes after initialization.
     */
    public void setActive() {
        lastActive = OffsetDateTime.now();
    }

    protected MessageManager getManager() {
        return manager;
    }
}
