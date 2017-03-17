package samurai.entities.dynamic.duel;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.entities.base.DynamicMessage;
import samurai.events.ReactionListener;
import samurai.util.MessageUtil;

import java.util.List;

/**
 * @author TonTL
 * @version 3/16/2017
 */
public class HangmanHelper extends DynamicMessage implements ReactionListener {


    private Hangman hangman;
    private List<String> reactions;

    HangmanHelper(Hangman hangman, List<String> reactions) {
        this.hangman = hangman;
        this.reactions = reactions;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Click a letter to start guessing.").build();
    }

    @Override
    protected void onReady(Message message) {
        MessageUtil.addReaction(message, reactions);
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        hangman.onReaction(event);
    }

    List<String> kill(MessageChannel channel) {
        unregister();
        channel.getMessageById(String.valueOf(getMessageId())).queue(message -> message.delete().queue());
        hangman = null;
//        List<String> leftovers = reactions;
//        reactions = null;
        return reactions;
    }
}
