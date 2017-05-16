/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package samurai.messages.impl.duel;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandFactory;
import samurai.command.basic.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.GenericCommandListener;
import samurai.messages.listeners.PrivateMessageListener;
import samurai.messages.listeners.ReactionListener;
import samurai.util.MessageUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HangmanGame extends DynamicMessage implements PrivateMessageListener, ReactionListener, GenericCommandListener {

    private static final List<String> HANGMAN_REACTIONS = Collections.unmodifiableList(Arrays.asList("\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF"));
    private static final List<String> HANGMAN_IMAGES = Collections.unmodifiableList(Arrays.asList("https://puu.sh/twqZM.jpg", "https://puu.sh/twr1V.jpg", "https://puu.sh/twr3V.jpg", "https://puu.sh/twrGc.jpg", "https://puu.sh/twrGx.jpg", "https://puu.sh/twrW2.jpg", "https://puu.sh/twrWn.jpg", "https://puu.sh/vRxTl.jpg", "https://puu.sh/twrWV.jpg", "https://puu.sh/twrX5.jpg", "https://puu.sh/twrXq.jpg"));


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

    public HangmanGame(Member author, String prefix) {
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
        helper.send(getManager());
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
    public void onCommand(Command command) {
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
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (word != null) return;
        if (!event.getAuthor().getId().equals(authorId)) return;

        word = CommandFactory.parseArgs(event.getMessage().getContent().trim()).stream().collect(Collectors.joining(" ")).toUpperCase(Locale.ENGLISH);
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
                .setFooter("SamuraiGames\u2122", Bot.info().AVATAR)
                .setImage(life == death ? HANGMAN_IMAGES.get(HANGMAN_IMAGES.size() - 1) : HANGMAN_IMAGES.get(life))
                .setColor(authorColor)
                .build();
    }
}