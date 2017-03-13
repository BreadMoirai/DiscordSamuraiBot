package samurai.core;

import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.Bot;
import samurai.SamuraiDiscord;
import samurai.command.Command;
import samurai.command.CommandFactory;

/**
 * Listener for SamuraiBot
 * This class listens to events from discord, takes the required information by building the appropriate command and passing it to SamuraiController
 *
 * @author TonTL
 * @version 4.0 - 2/16/2017
 */
public class CommandListener extends ListenerAdapter {
    private SamuraiDiscord samurai;

    CommandListener(SamuraiDiscord samurai) {
        this.samurai = samurai;
    }


    @Override
    public void onGenericGuildMessage(GenericGuildMessageEvent event) {
        if (!(event instanceof GuildMessageReceivedEvent || event instanceof GuildMessageUpdateEvent)) {
            return;
        }
        if (event.getAuthor().isFake()) return;
        if (event.getAuthor().getId().equals(Bot.ID)) {
            Bot.SENT.incrementAndGet();
            return;
        } else if (event.getAuthor().isBot()) return;

        final String prefix = samurai.getPrefix(Long.parseLong(event.getGuild().getId()));
        final Command c = CommandFactory.build(event, prefix);

        if (c != null) {
            samurai.onCommand(c);
        }
    }

}
