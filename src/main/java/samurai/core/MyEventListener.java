package samurai.core;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import samurai.core.command.Command;
import samurai.core.command.CommandFactory;
import samurai.core.command.admin.Groovy;
import samurai.core.events.GuildMessageEvent;
import samurai.core.events.PrivateMessageEvent;
import samurai.core.events.ReactionEvent;

/**
 * Listener for SamuraiBot
 * This class listens to events from discord, takes the required information by building the appropriate command and passing it to SamuraiController
 *
 * @author TonTL
 * @version 4.0 - 2/16/2017
 * @see Samurai
 */
public class MyEventListener implements EventListener {
    private final Samurai samurai;
    private final MessageManager messageManager;


    MyEventListener(JDA client) {
        messageManager = new MessageManager(client);
        samurai = new Samurai(messageManager);
        Groovy.addBinding("samurai", samurai);
    }


    void stop() {
        samurai.shutdown();
    }


    @Override
    public void onEvent(Event event) {
        if (event instanceof GenericGuildMessageEvent)
            if (event instanceof GuildMessageReceivedEvent || event instanceof GuildMessageUpdateEvent) {
                onGenericGuildMessageEvent((GenericGuildMessageEvent) event);
                Bot.CALLS.incrementAndGet();
                return;
            }
        if (event instanceof MessageReactionAddEvent) {
            onMessageReactionAdd((MessageReactionAddEvent) event);
            Bot.CALLS.incrementAndGet();
            return;
        }
        if (event instanceof GuildMemberJoinEvent) {
            onGuildMemberJoin((GuildMemberJoinEvent) event);
        }
        if (event instanceof GenericPrivateMessageEvent) {
            if (event instanceof PrivateMessageReceivedEvent || event instanceof PrivateMessageUpdateEvent) {
                onGenericPrivateMessageEvent((GenericPrivateMessageEvent) event);
            }
        }
        if (event instanceof ReadyEvent) {
            System.out.println("Ready!");
        }
    }

    private void onGenericPrivateMessageEvent(GenericPrivateMessageEvent event) {
        if (messageManager.hasChannelListener(Long.parseLong(event.getChannel().getId())))
            messageManager.onPrivateMessageEvent(new PrivateMessageEvent(event));

    }

    private void onGenericGuildMessageEvent(GenericGuildMessageEvent event) {
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

        if (messageManager.hasChannelListener(Long.parseLong(event.getChannel().getId()))) {
            messageManager.onMessageEvent(new GuildMessageEvent(event));
            messageManager.onCommand(c);
        }
    }


    private void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot() && messageManager.hasMessageListener(Long.parseLong(event.getMessageId()))) {
            final ReactionEvent r = new ReactionEvent(event);
            messageManager.onReaction(r);
        }
    }


    private void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild().getId().equals("233097800722808832")) {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById("267924616574533634"));
        }
    }


}
