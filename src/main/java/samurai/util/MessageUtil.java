package samurai.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.osu.entities.Score;

import java.awt.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class MessageUtil {

    public static Message build(Score s) {
        final MessageBuilder messageBuilder = new MessageBuilder();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.pink);
        StringBuilder sb = embedBuilder.getDescriptionBuilder();
        sb.append("**Player: **").append(s.getPlayer());
        sb.append("**\nScore: **").append(s.getScore());
        sb.append("**\nAccuracy: **").append(s.getAccuracy());
        sb.append("**\nGrade: **").append(s.getGrade());
        sb.append("**\nMD5: **").append(s.getBeatmapHash());
        embedBuilder.setTimestamp(Instant.ofEpochSecond(s.getTimestamp()));
        return messageBuilder.setEmbed(embedBuilder.build()).build();
    }

    public static void addReaction(Message message, Collection<String> s) {
        s.forEach(s1 -> message.addReaction(s1).queue());
    }

    public static void addReaction(Message message, Collection<String> s, Consumer<Void> consumer) {
        final Iterator<String> iterator = s.iterator();
        int i = 0;
        while (i++ != s.size()-1) {
            message.addReaction(iterator.next()).queue(null ,null);
        }
        message.addReaction(iterator.next()).queue(consumer, null);
    }

    public static Message of(String s) {
        return new MessageBuilder().append(s).build();
    }

}
