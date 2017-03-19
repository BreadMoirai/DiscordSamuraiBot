package samurai.messages.listeners;

import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;

/**
 * @author TonTL
 * @version 3/15/2017
 */
public interface ChannelMessageListener extends SamuraiListener {
    void onGuildMessageEvent(GenericGuildMessageEvent event);
}
