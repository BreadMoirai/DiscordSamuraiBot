package samurai.core.events.listeners;

import samurai.core.events.PrivateMessageEvent;

/**
 * Listens to all private messages sent to SamuraiBot in the corresponding channel of the dynamic message
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 * @see PrivateMessageEvent
 */
public interface PrivateListener {

    void onPrivateMessageEvent(PrivateMessageEvent event);
}
