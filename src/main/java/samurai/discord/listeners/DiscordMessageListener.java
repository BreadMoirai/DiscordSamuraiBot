package samurai.discord.listeners;

import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.SamuraiDiscord;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class DiscordMessageListener extends ListenerAdapter {

    private SamuraiDiscord samurai;

    public DiscordMessageListener(SamuraiDiscord samurai) {
        this.samurai = samurai;
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        samurai.onMessageDelete(Long.parseLong(event.getChannel().getId()), Long.parseLong(event.getMessageId()));
    }

    @Override
    public void onPrivateMessageDelete(PrivateMessageDeleteEvent event) {
        samurai.onMessageDelete(Long.parseLong(event.getChannel().getId()), Long.parseLong(event.getMessageId()));
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        samurai.onChannelDelete(Long.parseLong(event.getChannel().getId()));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        samurai.getMessageManager().onGuildMessageEvent(event);
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        samurai.getMessageManager().onGuildMessageEvent(event);
    }

}
