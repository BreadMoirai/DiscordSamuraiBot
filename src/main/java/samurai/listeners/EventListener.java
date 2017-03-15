//package samurai.core;
//
//import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
//import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
//import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
//import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
//import net.dv8tion.jda.core.hooks.ListenerAdapter;
//import samurai.Bot;
//import samurai.SamuraiDiscord;
//import samurai.command.Command;
//import samurai.command.CommandFactory;
//import samurai.events.ReactionEvent;
//
///**
// * @author TonTL
// * @version 3/14/2017
// */
//public class EventListener extends ListenerAdapter {
//    private SamuraiDiscord samurai;
//
//    public EventListener(SamuraiDiscord samurai) {
//        this.samurai = samurai;
//    }
//
//
//    @Override
//    public void onGenericGuildMessage(GenericGuildMessageEvent event) {
//        if (!(event instanceof GuildMessageReceivedEvent || event instanceof GuildMessageUpdateEvent)) {
//            return;
//        }
//        if (event.getAuthor().isFake()) return;
//        if (event.getAuthor().getId().equals(Bot.ID)) {
//            Bot.SENT.incrementAndGet();
//            return;
//        } else if (event.getAuthor().isBot()) return;
//
//        final String prefix = samurai.getPrefix(Long.parseLong(event.getGuild().getId()));
//        final Command c = CommandFactory.build(event, prefix);
//
//        if (c != null) {
//            c.getContext().setGuild(samurai.getGuildManager().getGuild(c.getContext().getGuildId()));
//            samurai.onCommand(c);
//
//        }
//    }
//
//    @Override
//    public void onMessageReactionAdd(MessageReactionAddEvent event) {
//        if (!event.getUser().isBot()) {
//            final ReactionEvent r = new ReactionEvent(event);
//            samurai.onReaction(r);
//        }
//    }
//}
