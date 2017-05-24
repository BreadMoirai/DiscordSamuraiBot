package samurai.messages.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.Bot;
import samurai.SamuraiDiscord;
import samurai.command.CommandContext;
import samurai.command.basic.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;
import samurai.messages.listeners.ReactionListener;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class SimplePoll extends DynamicMessage implements ReactionListener, Reloadable {

    private static final long serialVersionUID = 156L;

    private static final long YES;
    private static final long NO;

    private static final float H_BEGIN = 240.0f / 360.0f;
    private static final float SATURATION = .6f;
    private static final float BALANCE = .9f;

    static {
        final Config config = ConfigFactory.load("source_commands.conf");
        YES = config.getLong("SuggestionPoll.yes");
        NO = config.getLong("SuggestionPoll.no");
    }

    private String content;
    private String author;
    private String authorUrl;
    private Instant timestamp;

    public SimplePoll() {
    }

    public SimplePoll(String content, Member author, Instant timestamp) {
        this.content = content;
        this.author = author.getEffectiveName();
        this.authorUrl = author.getUser().getEffectiveAvatarUrl();
        this.timestamp = timestamp;
    }

    @Override
    protected Message initialize() {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Poll", null);
        eb.setDescription(content);
        eb.setTimestamp(timestamp);
        eb.setFooter(author, authorUrl);
        final Color hsbColor = new Color(Color.HSBtoRGB(H_BEGIN, SATURATION, BALANCE));
        eb.setColor(hsbColor);
        return new MessageBuilder().setEmbed(eb.build()).build();
    }

    @Override
    protected void onReady(Message message) {
        message.addReaction(message.getGuild().getEmoteById(YES)).queue();
        message.addReaction(message.getGuild().getEmoteById(NO)).queue();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final long reactionAdded = event.getReaction().getEmote().getIdLong();
        final User user = event.getUser();
        //noinspection Duplicates
        event.getTextChannel().getMessageById(getMessageId()).queue(message -> {
            int y = 0, n = 0;
            for (MessageReaction messageReaction : message.getReactions()) {
                final long id = messageReaction.getEmote().getIdLong();
                final int count = messageReaction.getCount();
                if (id == YES) y = count;
                else if (id == NO) n = count;
                if (reactionAdded != id) {
                    messageReaction.getUsers().queue(users -> {
                        if (users.contains(user)) {
                            messageReaction.removeReaction(user).queue();
                        }
                    });
                }
            }
            final Color hsbColor = getColor(y, n);
            message.editMessage(new EmbedBuilder(message.getEmbeds().get(0)).setColor(hsbColor).build()).queue();
        });
    }

    private Color getColor(int yes, int no) {
        float hue = (float) (240.0f + (180.0f * Math.atan((yes - no) / 2.0f) / Math.PI)) / 360.0f;
        return new Color(Color.HSBtoRGB(hue, SATURATION, BALANCE));
    }

    @Override
    public void reload(SamuraiDiscord samuraiDiscord) {
        replace(samuraiDiscord.getMessageManager(), getMessageId());
    }
}
