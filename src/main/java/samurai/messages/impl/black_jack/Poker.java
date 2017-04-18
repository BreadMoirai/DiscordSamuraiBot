package samurai.messages.impl.black_jack;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ChannelMessageListener;
import samurai.messages.listeners.ReactionListener;
import samurai.util.MessageUtil;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 3/7/2017
 */
public class Poker extends DynamicMessage implements ChannelMessageListener, ReactionListener {

    private static final String BUY_IN = "\uD83D\uDCB8";
    private static ArrayList<Member> players;

    @Override
    protected Message initialize() {
        return MessageUtil.of("Stacking the odds...");
    }

    @Override
    protected void onReady(Message message) {
        message.addReaction(BUY_IN).queue();
        message.editMessage("Click \uD83D\uDCB8 to buy in").queue();
    }

    @Override
    public void onGuildMessageEvent(GenericGuildMessageEvent event) {

    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        if (event.getReaction().getEmote().getName().equals(BUY_IN) && event.getChannel().getType() == ChannelType.TEXT) {
            players.add(((TextChannel) event.getChannel()).getGuild().getMember(event.getUser()));
        }
    }
}
