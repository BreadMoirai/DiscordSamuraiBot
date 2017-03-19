package samurai.messages.listeners;

import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;

/**
 * @author TonTL
 * @version 3/15/2017
 */
public interface PrivateMessageListener extends SamuraiListener {
    void onPrivateMessageEvent(GenericPrivateMessageEvent event);
}
