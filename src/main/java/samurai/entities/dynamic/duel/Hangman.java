
package samurai.entities.dynamic.duel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.entities.base.DynamicMessage;
import samurai.events.PrivateMessageListener;
import samurai.events.ReactionListener;
import samurai.util.MessageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Hangman extends DynamicMessage implements PrivateMessageListener, ReactionListener {

    private static final List<String> HANGMAN_REACTIONS = Collections.unmodifiableList(Arrays.asList("🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯", "🇰", "🇱", "🇲", "🇳", "🇴", "🇵", "🇶", "🇷", "🇸", "🇹", "🇺", "🇻", "🇼", "🇽", "🇾", "🇿"));
    private static final List<String> HANGMAN_IMAGES = Collections.unmodifiableList(Arrays.asList("https://puu.sh/twqZM.jpg", "https://puu.sh/twr1V.jpg", "https://puu.sh/twr3V.jpg", "https://puu.sh/twrGc.jpg", "https://puu.sh/twrGx.jpg", "https://puu.sh/twrW2.jpg", "https://puu.sh/twrWn.jpg", "https://puu.sh/twrWJ.jpg", "https://puu.sh/twrWV.jpg", "https://puu.sh/twrX5.jpg", "https://puu.sh/twrXq.jpg"));

    private Member author;
    private String word;
    private String display;
    private static ArrayList<String> reactions;
    private static HangmanHelper helper;

    public Hangman(Member author) {
        this.author = author;
        reactions = new ArrayList<>(HANGMAN_REACTIONS);
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("PM me your word. ;)").build();
    }

    @Override
    protected void onReady(Message message) {
        MessageUtil.addReaction(message, reactions.subList(0, 15));
        helper = new HangmanHelper(this, reactions.subList(15, reactions.size()));
        helper.setChannelId(getChannelId());
        helper.onReady(getManager());
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final MessageReaction reaction = event.getReaction();
        final String name = reaction.getEmote().getName();

        if (!reactions.contains(name)) {
            return;
        }
        char letter = (char) (HANGMAN_REACTIONS.indexOf(name) + 65);
        System.out.println(letter);
        int idx = 0;
        while((idx = name.indexOf(letter, idx)) != -1) {
            String before = display.substring(0 ,idx);
            String after = display.substring(++idx);
            display = before + letter + after;
        }
        event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage(display).queue());

        reaction.getUsers().queue(users -> users.forEach(user -> reaction.removeReaction(user).queue()));
        reactions.remove(name);
        if (reactions.size() < 20) {
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> MessageUtil.addReaction(message, helper.kill(event.getJDA())));
        }
    }

    @Override
    public void onPrivateMessageEvent(GenericPrivateMessageEvent event) {
        if (event.getAuthor().getId().equals(author.getUser().getId())) {
            word = event.getMessage().getContent().trim().toUpperCase();
            event.getChannel().sendMessage("Recieved Word: `" + word + '`').queue();
        }
        event.getJDA().getTextChannelById(String.valueOf(getChannelId())).getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage((display = word.replaceAll("[A-Z]", "*_* "))).queue());
    }

}