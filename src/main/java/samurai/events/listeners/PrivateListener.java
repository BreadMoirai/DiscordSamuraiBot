package samurai.events.listeners;

import samurai.events.PrivateMessageEvent;

/**
 * Listens to all private messages sent to SamuraiBot in the corresponding channel of the dynamic message
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 * @see PrivateMessageEvent
 */
public interface PrivateListener extends SamuraiListener {

    void onPrivateMessageEvent(PrivateMessageEvent event);
}
