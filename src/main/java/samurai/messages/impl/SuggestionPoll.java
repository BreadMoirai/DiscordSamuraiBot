package samurai.messages.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.Bot;
import samurai.command.CommandContext;
import samurai.command.basic.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ReactionListener;

import java.awt.*;
import java.time.OffsetDateTime;

public class SuggestionPoll extends DynamicMessage implements ReactionListener {

    private static final long YES;
    private static final long NO;
    private static final long TODO;
    private static final long MAYBE;

    private static final Color COLOR_YES, COLOR_NO;
    private static final float H_BEGIN = 240.0f / 360.0f;
    private static final float H_OFFSET = 165.0f / 360.0f / 0.2f;
    private static final float SATURATION = .6f;
    private static final float BALANCE = .9f;

    static {
        final Config config = ConfigFactory.load("source_commands.conf");
        YES = config.getLong("SuggestionPoll.yes");
        NO = config.getLong("SuggestionPoll.no");
        MAYBE = config.getLong("SuggestionPoll.maybe");
        TODO = config.getLong("SuggestionPoll.todo");
        COLOR_NO = new Color(232, 28, 28);
        COLOR_YES = new Color(58, 242, 12);
    }

    private final String type;
    private final String content;
    private final Member author;
    private final OffsetDateTime timestamp;

    public SuggestionPoll(String type, String content, Member author, OffsetDateTime timestamp) {
        this.type = type;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
    }

    @Override
    protected Message initialize() {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(type, null);
        eb.setAuthor(author.getEffectiveName(), null, author.getUser().getEffectiveAvatarUrl());
        eb.setDescription(content);
        eb.setTimestamp(timestamp);
        eb.setFooter("SUBMITTED", "https://cdn.discordapp.com/emojis/" + MAYBE + ".png");
        final Color hsbColor = new Color(Color.HSBtoRGB(H_BEGIN, SATURATION, BALANCE));
        System.out.println("hsbColor = " + hsbColor);
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
        if (user.getIdLong() == Bot.info().OWNER) {
            if (reactionAdded == YES) {
                event.getTextChannel().getMessageById(getMessageId()).queue(message -> {
                    final EmbedBuilder eb = new EmbedBuilder(message.getEmbeds().get(0));
                    eb.setFooter("APPROVED", "https://cdn.discordapp.com/emojis/" + YES + ".png");
                    eb.setColor(COLOR_YES);
                    final MessageEmbed embed = eb.build();
                    message.editMessage(embed).queue();
                    unregister();
                    final CommandContext context = new CommandContext(null, embed.getTitle(), null, null, null, null, embed.getDescription() + " - _" + embed.getAuthor().getName() + "_", null, event.getGuild().getIdLong(), TODO, 0, null, null);
                    final GenericCommand genericCommand = new GenericCommand();
                    genericCommand.setContext(context);
                    getManager().onCommand(genericCommand);
                });
            } else if (reactionAdded == NO) {
                event.getTextChannel().getMessageById(getMessageId()).queue(message -> {
                    final EmbedBuilder eb = new EmbedBuilder(message.getEmbeds().get(0));
                    final StringBuilder db = eb.getDescriptionBuilder();
                    db.insert(0, "~~");
                    db.append("~~");
                    eb.setFooter( "DENIED", "https://cdn.discordapp.com/emojis/" + NO + ".png");
                    eb.setColor(COLOR_NO);
                    message.editMessage(eb.build()).queue();
                    unregister();
                });
            }
        } else if (user.equals(author.getUser()) && reactionAdded == NO) {
            event.getTextChannel().deleteMessageById(getMessageId()).queue();
            unregister();
        } else {
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
    }

    private Color getColor(int yes, int no) {
        float hue = (float) (240.0f + (180.0f*Math.atan((yes-no)/2.0f)/Math.PI)) / 360.0f;
        return new Color(Color.HSBtoRGB(hue, SATURATION, BALANCE));
    }

}
