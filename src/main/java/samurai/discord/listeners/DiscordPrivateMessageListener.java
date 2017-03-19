package samurai.discord.listeners;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.Bot;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
public class DiscordPrivateMessageListener extends ListenerAdapter {
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        Bot.onPrivateMessageEvent(event);
    }

    @Override
    public void onPrivateMessageUpdate(PrivateMessageUpdateEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        Bot.onPrivateMessageEvent(event);
    }
}
