package samurai.core.events.listeners;

import samurai.core.events.GuildMessageEvent;

/**
 * Listens to all messages sent within a channel
 * @author TonTL
 * @version 4.x - 3/10/2017
 * @see GuildMessageEvent
 */
public interface MessageListener {

    void onMessageEvent(GuildMessageEvent event);

}
