
package samurai.entities.dynamic.duel;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.Bot;
import samurai.command.CommandFactory;
import samurai.command.GenericCommand;
import samurai.entities.base.DynamicMessage;
import samurai.events.GenericCommandListener;
import samurai.events.PrivateMessageListener;
import samurai.events.ReactionListener;
import samurai.util.MessageUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Hangman extends DynamicMessage implements PrivateMessageListener, ReactionListener, GenericCommandListener {

    private static final List<String> HANGMAN_REACTIONS = Collections.unmodifiableList(Arrays.asList("🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯", "🇰", "🇱", "🇲", "🇳", "🇴", "🇵", "🇶", "🇷", "🇸", "🇹", "🇺", "🇻", "🇼", "🇽", "🇾", "🇿"));
    private static final List<String> HANGMAN_IMAGES = Collections.unmodifiableList(Arrays.asList("https://puu.sh/twqZM.jpg", "https://puu.sh/twr1V.jpg", "https://puu.sh/twr3V.jpg", "https://puu.sh/twrGc.jpg", "https://puu.sh/twrGx.jpg", "https://puu.sh/twrW2.jpg", "https://puu.sh/twrWn.jpg", "https://puu.sh/twrWJ.jpg", "https://puu.sh/twrWV.jpg", "https://puu.sh/twrX5.jpg", "https://puu.sh/twrXq.jpg"));


    private final String authorName;
    private final Color authorColor;
    private final String authorId;
    private final String authorAvatar;
    private final String prefix;
    private final CopyOnWriteArrayList<String> reactions;
    private final ConcurrentSkipListSet<Integer> hidden;
    private String word;
    private HangmanHelper helper;
    private int life, death;

    public Hangman(Member author, String prefix) {
        this.authorColor = author.getColor();
        this.authorName = author.getEffectiveName();
        this.authorAvatar = author.getUser().getEffectiveAvatarUrl();
        this.authorId = author.getUser().getId();
        this.prefix = prefix;
        reactions = new CopyOnWriteArrayList<>(HANGMAN_REACTIONS);
        hidden = new ConcurrentSkipListSet<>();
        life = 0;
        death = 10;
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
        //if (event.getUser().getId().equals(authorId)) return;
        if (word == null) return;

        final MessageReaction reaction = event.getReaction();
        final String name = reaction.getEmote().getName();

        if (!reactions.contains(name)) {
            return;
        }
        reaction.removeReaction().queue();
        reaction.removeReaction(event.getUser()).queue();
        reactions.remove(name);
        if (reactions.size() < 20 && helper != null) {
            helper.kill(event.getChannel());
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> reactions.forEach(s -> message.addReaction(s).queue()));
            helper = null;
        }

        char letter = (char) (HANGMAN_REACTIONS.indexOf(name) + 65);

        int letterIdx = word.indexOf(letter);
        StringBuilder sb = new StringBuilder();
        if (letterIdx == -1) {
            life++;
            sb.append("Oops! **").append(letter).append("** was not found within the word");
        } else {
            hidden.removeIf(integer -> word.charAt(integer) == letter);
            if (hidden.isEmpty()) {
                sb.append("\uD83C\uDF8A ")
                        .append(event.getUser().getAsMention())
                        .append(" has completed the word: `")
                        .append(word).append("`\n\n\n");
                event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.clearReactions().queue());
                unregister();
                if (helper != null) {
                    helper.kill(event.getChannel());
                    helper = null;
                }
            } else {
                sb.append("You can use `").append(prefix).append("guess word` to try and guess.\n").append("Careful though, each wrong guess is another step towards death!");
            }
        }
        event.getChannel().getMessageById(String.valueOf(getMessageId()))
                .queue(message -> message.editMessage(
                        buildEmbed(sb.toString())
                ).queue());
        if (life == death) {
            unregister();
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.clearReactions().queue());
            if (helper != null) {
                helper.kill(event.getChannel());
                helper = null;
            }
        }
    }

    @Override
    public void onCommand(GenericCommand command) {
        if (word == null) return;
        if (command.getContext().getKey().equalsIgnoreCase("guess")) {
            if (command.getContext().getContent().trim().equalsIgnoreCase(word)) {
                hidden.clear();
                StringBuilder sb = new StringBuilder().append("\uD83C\uDF8A**").append(command.getContext().getAuthor().getEffectiveName()).append("** has guessed correctly!\uD83C\uDF8A\n");
                command.getContext().getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> {
                    message.clearReactions().queue();
                    message.editMessage(buildEmbed(sb.toString())).queue();
                });
                unregister();
                if (helper != null) {
                    helper.kill(command.getContext().getChannel());
                    helper = null;
                }
            } else {
                life++;
                if (life == death) {
                    unregister();
                    command.getContext().getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.clearReactions().queue());
                    if (helper != null) {
                        helper.kill(command.getContext().getChannel());
                        helper = null;
                    }
                }
                command.getContext().getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage(buildEmbed("Bad guess " + command.getContext().getAuthor().getAsMention() + ". Better luck next time.")).queue());
            }
        }
    }


    @Override
    public void onPrivateMessageEvent(GenericPrivateMessageEvent event) {
        if (word != null) return;
        if (!event.getAuthor().getId().equals(authorId)) return;

        word = CommandFactory.parseArgs(event.getMessage().getContent().trim()).stream().collect(Collectors.joining(" ")).toUpperCase();
        event.getChannel().sendMessage("Recieved Word: `" + word + '`').queue();
        IntStream.range(0, word.length()).boxed().forEach(hidden::add);
        hidden.removeIf(integer -> !Character.isLetter(word.charAt(integer)));

        event.getJDA().getTextChannelById(String.valueOf(getChannelId())).getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage(buildEmbed("I have received a secret transmission. Just try and decode it!")).queue());
    }

    private String buildWord() {
        boolean prevIC = false;

        StringBuilder sb = new StringBuilder().append('`');
        for (int i = 0; i < word.length(); i++) {
            final char ch = word.charAt(i);
            if (Character.isLetter(ch)) {
                if (prevIC) sb.append(' ');
                if (hidden.contains(i)) {
                    sb.append("_");
                } else {
                    sb.append(ch);
                }
                prevIC = true;
            } else if (Character.isWhitespace(ch)) {
                sb.append("   ");
                prevIC = false;
            } else {
                sb.append(' ').append(ch).append(' ');
                prevIC = false;
            }
        }
        return sb.append('`').toString();
    }

    private MessageEmbed buildEmbed(String s) {
        return new EmbedBuilder()
                .setDescription(s)
                .addField("", buildWord(), true)
                .setAuthor(authorName, null, authorAvatar)
                .setFooter("SamuraiGames\u2122", Bot.AVATAR)
                .setImage(life == death ? HANGMAN_IMAGES.get(HANGMAN_IMAGES.size() - 1) : HANGMAN_IMAGES.get(life))
                .setColor(authorColor)
                .build();
    }
}