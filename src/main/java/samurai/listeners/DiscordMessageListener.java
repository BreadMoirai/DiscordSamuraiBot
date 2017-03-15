package samurai.listeners;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.SamuraiDiscord;
import samurai.events.GuildMessageEvent;
import samurai.events.PrivateMessageEvent;

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
        if (!isHuman(event.getAuthor())) return;
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
        if (!isHuman(event.getAuthor())) return;
        samurai.onGuildMessageEvent(new GuildMessageEvent(event));
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (!isHuman(event.getAuthor())) return;
        samurai.onGuildMessageEvent(new GuildMessageEvent(event));
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (!isHuman(event.getAuthor())) return;
        samurai.onPrivateMessageEvent(new PrivateMessageEvent(event));
    }

    @Override
    public void onPrivateMessageUpdate(PrivateMessageUpdateEvent event) {
        if (!isHuman(event.getAuthor())) return;
        samurai.onPrivateMessageEvent(new PrivateMessageEvent(event));
    }

    private boolean isHuman(User u) {
        return !u.isBot() && !u.isFake();
    }
}
