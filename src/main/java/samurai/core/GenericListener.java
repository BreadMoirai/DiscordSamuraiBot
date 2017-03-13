package samurai.core;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import samurai.Bot;
import samurai.events.PrivateMessageEvent;
import samurai.events.ReactionEvent;
import samurai.osu.OsuTracker;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class GenericListener implements EventListener{


    @Override
    public void onEvent(Event event) {
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
        if (event instanceof UserGameUpdateEvent) {
            onUserGameUpdateEvent((UserGameUpdateEvent) event);
        }
        if (event instanceof ReadyEvent) {
            System.out.println("Ready!");
        }
    }

    private void onUserGameUpdateEvent(UserGameUpdateEvent event) {
        final User user = event.getUser();
        final Game game = event.getGuild().getMember(user).getGame();
        if (game == null) return;
        if (game.getName().equalsIgnoreCase("osu!")) {
            OsuTracker.trackUser(user);
        } else if (event.getPreviousGame().getName().equals("osu!")) {
            .untrackUser(user);
        }
    }

    private void onGenericPrivateMessageEvent(GenericPrivateMessageEvent event) {
        if (messageManager.hasChannelListener(Long.parseLong(event.getChannel().getId())))
            messageManager.onPrivateMessageEvent(new PrivateMessageEvent(event));

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
