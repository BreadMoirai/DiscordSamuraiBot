package samurai.messages.impl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.SamuraiDiscord;
import samurai.messages.MessageManager;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;
import samurai.messages.listeners.ReactionListener;
import samurai.points.PointTracker;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

public class RollPoll extends DynamicMessage implements ReactionListener, Reloadable {

    private static final long serialVersionUID = 777L;

    private static final String DICE = "\uD83C\uDFB2", END = "\uD83C\uDFC1";
    private static final String[] MEDAL = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};

    private final Map<Long, Integer> rolls;
    private transient long time;
    private transient TimeUnit unit;
    private Instant endTime;
    private long pointValue;
    private transient PointTracker pointTracker;

    {
        rolls = new HashMap<>();
    }

    public RollPoll(long time, TimeUnit unit, long pointValue, PointTracker pointTracker) {
        this.time = time;
        this.unit = unit;
        this.pointValue = pointValue;
        this.pointTracker = pointTracker;
    }

    public RollPoll() {
        this.time = 0;
        this.unit = null;
    }

    @Override
    protected Message initialize() {
        final MessageBuilder mb = new MessageBuilder();
        mb.append("Click ").append(DICE).append(" to roll");
        if (pointTracker != null && pointValue > 0) {
            mb.append(" for a maximum prize of **").append(pointValue).append("**");
        }
        mb.append('!');
        return mb.build();
    }

    @Override
    protected void onReady(Message message) {
        message.addReaction(DICE).queue();
        if (time > 0) {
            message.getChannel().getMessageById(getMessageId()).queueAfter(time, unit, message1 -> {
                final Map.Entry<Long, Integer> memberIntegerEntry = rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElse(null);
                if (memberIntegerEntry == null) {
                    message1.editMessage("No Winner...").queue();
                    message1.clearReactions().queue();
                    unregister();
                    return;
                }
                long winner = memberIntegerEntry.getKey();
                if (pointTracker != null && pointValue > 0) {
                    final Member memberById = message1.getGuild().getMemberById(winner);
                    message1.editMessage(new MessageBuilder().append("The Winner is... \uD83C\uDF8A").append((memberById != null ? memberById.getAsMention() : "unknown")).append("\uD83C\uDF8A").setEmbed(distDispPoints(pointValue, message1.getGuild())).build()).queue();
                } else {
                    final Member memberById = message1.getGuild().getMemberById(winner);
                    message1.editMessage("Winner is \uD83C\uDF8A" + (memberById != null ? memberById.getAsMention() : "unknown") + "\uD83C\uDF8A").queue();
                }
                message1.clearReactions().queue();
                unregister();
            });
            if (endTime == null)
                endTime = message.getCreationTime().plus(time, ChronoUnit.valueOf(unit.name())).toInstant();
        } else {
            message.addReaction(END).queue();
        }
        message.editMessage(buildScoreBoard(message.getGuild())).queue();
    }

    private Message buildScoreBoard(Guild g) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        if (endTime != null) {
            embedBuilder.setFooter("End Time", null);
            embedBuilder.setTimestamp(endTime);
        }
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        AtomicInteger i = new AtomicInteger(1);
        rolls.entrySet().stream().sorted(Comparator.comparingInt((ToIntFunction<Map.Entry<Long, Integer>>) Map.Entry::getValue).reversed()).forEachOrdered(memberIntegerEntry -> {
            final int pos = i.getAndIncrement();
            if (pos <= 3) {
                description.append(MEDAL[pos - 1]).append(' ');
            } else {
                description.append(String.format("`%02d.` ", pos));
            }
            final Member memberById = g.getMemberById(memberIntegerEntry.getKey());
            description.append((memberById != null ? memberById.getEffectiveName() : "unknown")).append(" rolled a **").append(memberIntegerEntry.getValue())
                    .append("**\n");
        });
        final MessageBuilder mb = new MessageBuilder().append("Click ").append(DICE).append(" to roll");
        if (pointTracker != null && pointValue > 0) {
            mb.append(" for a maximum prize of **").append(String.valueOf(pointValue)).append("**");
        }
        mb.append('!');
        return mb.setEmbed(embedBuilder.build()).build();
    }

    private MessageEmbed distDispPoints(long value, Guild g) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        if (endTime != null) {
            embedBuilder.setTimestamp(endTime);
        }
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        final long guildId = getGuildId();
        int i = 1;
        List<Map.Entry<Long, Integer>> toSort = new ArrayList<>();
        toSort.addAll(rolls.entrySet());
        toSort.sort(Comparator.comparingInt((ToIntFunction<Map.Entry<Long, Integer>>) Map.Entry::getValue).reversed());
        int previous = toSort.get(0).getValue();
        for (Map.Entry<Long, Integer> memberRoll : toSort) {
            int pos = i;
            value *= (double) memberRoll.getValue() / (double) previous;
            pointTracker.offsetPoints(guildId, memberRoll.getKey(), value);

            if (pos <= 3) description.append(MEDAL[pos - 1]).append(' ');
            else description.append(String.format("`%02d.` ", pos));

            final Member memberById = g.getMemberById(memberRoll.getKey());
            description.append((memberById != null ? memberById.getEffectiveName() : "unknown"))
                    .append(" rolled a **").append(memberRoll.getValue())
                    .append("** to gain __").append(value).append("__ points\n");
            if (i == 1) value *= 0.67;
            i++;
            previous = memberRoll.getValue();
        }
        description.deleteCharAt(description.length() - 1);
        return embedBuilder.build();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        if (event.getReaction().getEmote().getName().equals(DICE)) {
            final Member member = event.getMember();
            rolls.putIfAbsent(member.getUser().getIdLong(), ThreadLocalRandom.current().nextInt(101));
            event.getTextChannel().editMessageById(getMessageId(), buildScoreBoard(event.getGuild())).queue();
        } else if (event.getUser().getIdLong() == getAuthorId() && event.getReactionEmote().getName().equals(END)) {
            final Map.Entry<Long, Integer> memberIntegerEntry = rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElse(null);
            TextChannel textChannel = event.getTextChannel();
            if (textChannel != null) {
                if (memberIntegerEntry == null) {
                    textChannel.editMessageById(getMessageId(), "No Winner...").queue();
                    textChannel.clearReactionsById(getMessageId()).queue();
                    unregister();
                    return;
                }
                long winner = memberIntegerEntry.getKey();
                final Guild guild = textChannel.getGuild();
                if (pointTracker != null && pointValue > 0) {
                    final Member memberById = guild.getMemberById(winner);
                    textChannel.editMessageById(getMessageId(), new MessageBuilder().append("The Winner is... \uD83C\uDF8A").append((memberById != null ? memberById.getAsMention() : "unknown")).append("\uD83C\uDF8A").setEmbed(distDispPoints(pointValue, guild)).build()).queue();
                } else {
                    final Member memberById = guild.getMemberById(winner);
                    textChannel.editMessageById(getMessageId(), "Winner is \uD83C\uDF8A" + (memberById != null ? memberById.getAsMention() : "unknown") + "\uD83C\uDF8A").queue();
                }
                textChannel.clearReactionsById(getMessageId()).queue();
                unregister();
            }
            unregister();
        }
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void reload(SamuraiDiscord samuraiDiscord) {
        if (pointValue > 0)
            this.pointTracker = samuraiDiscord.getPointTracker();
        if (endTime != null) {
            unit = TimeUnit.SECONDS;
            time = ChronoUnit.SECONDS.between(Instant.now(), endTime);
            if (time < 0) {
                replace(samuraiDiscord.getMessageManager(), getMessageId());
            }
        } else {
            replace(samuraiDiscord.getMessageManager(), getMessageId());
        }
    }

    @Override
    public String toString() {
        return "RollPoll{" + "rolls=" + rolls +
                ", time=" + time +
                ", unit=" + unit +
                ", endTime=" + endTime +
                ", pointValue=" + pointValue +
                ", pointTracker=" + pointTracker +
                '}';
    }
}
