package samurai.messages.impl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ReactionListener;
import samurai.points.PointTracker;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

public class RollPoll extends DynamicMessage implements ReactionListener {

    private static final String DICE = "\uD83C\uDFB2", END = "\uD83C\uDFC1";
    private static final String[] MEDAL = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};

    private final Map<Member, Integer> rolls;
    private final int time;
    private TimeUnit unit;
    private OffsetDateTime endTime;
    private boolean rollypolly;
    private int pointValue;
    private PointTracker pointTracker;

    {
        rolls = new HashMap<>(50);
        endTime = null;
    }

    public RollPoll(int time, TimeUnit unit, boolean rollypolly, int pointValue, PointTracker pointTracker) {
        this.time = time;
        this.unit = unit;
        this.rollypolly = rollypolly;
        this.pointValue = pointValue;
        this.pointTracker = pointTracker;
    }

    public RollPoll() {
        this.time = 0;
        this.unit = null;
        rollypolly = false;
    }

    @Override
    protected Message initialize() {
        final StringBuilder mb = new StringBuilder();
        mb.append("Click ").append(DICE).append(" to roll");
        if (pointTracker != null && pointValue > 0) {
            mb.append(" for a maximum prize of **").append(pointValue).append("**");
        }
        mb.append('!');
        return new MessageBuilder().append(mb).build();
    }

    @Override
    protected void onReady(Message message) {
        message.addReaction(DICE).queue();
        if (time > 0) {
            message.getChannel().getMessageById(getMessageId()).queueAfter(time, unit, message1 -> {
                Member winner = rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElseGet(null).getKey();
                if (pointTracker != null && pointValue > 0) {
                    message1.editMessage(new MessageBuilder().append("The Winner is... \uD83C\uDF8A").append(winner.getAsMention()).append("\uD83C\uDF8A").setEmbed(distDispPoints()).build()).queue();
                } else {
                    message1.editMessage("Winner is \uD83C\uDF8A" + winner.getAsMention() + "\uD83C\uDF8A").queue();
                }
                message1.clearReactions().queue();
                unregister();
            });
            endTime = message.getCreationTime().plus(time, ChronoUnit.valueOf(unit.name()));
        } else {
            message.addReaction(END).queue();
        }
        message.editMessage(buildScoreBoard()).queue();
        unit = null;
    }

    private MessageEmbed distDispPoints() {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        if (endTime != null) {
            embedBuilder.setTimestamp(endTime);
        }
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        final long guildId = getGuildId();
        int i = 1;
        double value = pointValue;
        List<Map.Entry<Member, Integer>> toSort = new ArrayList<>();
        toSort.addAll(rolls.entrySet());
        toSort.sort(Comparator.comparingInt((ToIntFunction<Map.Entry<Member, Integer>>) Map.Entry::getValue).reversed());
        int previous = toSort.get(0).getValue();
        for (Map.Entry<Member, Integer> memberIntegerEntry : toSort) {
            int pos = i;
            value *= memberIntegerEntry.getValue() / previous;
            pointTracker.offsetPoints(guildId, memberIntegerEntry.getKey().getUser().getIdLong(), value);
            if (pos <= 3) {
                description.append(MEDAL[pos - 1]).append(' ');
            } else {
                description.append(String.format("`%02d.`", pos));
            }
            description.append(String.format("%s rolled a **%d** to gain __%.2f__ points\n", memberIntegerEntry.getKey().getEffectiveName(), memberIntegerEntry.getValue(), value));
            if (i == 1) value *= 0.67;
            i++;
            previous = memberIntegerEntry.getValue();
        }
        return embedBuilder.build();
    }

    private Message buildScoreBoard() {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        if (endTime != null) {
            embedBuilder.setTimestamp(endTime);
        }
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        AtomicInteger i = new AtomicInteger(1);
        rolls.entrySet().stream().sorted(Comparator.comparingInt((ToIntFunction<Map.Entry<Member, Integer>>) Map.Entry::getValue).reversed()).forEachOrdered(memberIntegerEntry -> {
            final int pos = i.getAndIncrement();
            if (pos <= 3) {
                description.append(MEDAL[pos - 1]).append(' ');
            } else {
                description.append(String.format("`%02d.`", pos));
            }
            description.append(memberIntegerEntry.getKey().getEffectiveName()).append(" rolled a ").append(memberIntegerEntry.getValue())
                    .append('\n');
        });
        final MessageBuilder mb = new MessageBuilder().append("Click ").append(DICE).append(" to roll");
        if (pointTracker != null && pointValue > 0) {
            mb.append("for a maximum prize of **").append(String.valueOf(pointValue)).append("**");
        }
        mb.append('!');
        return mb.setEmbed(embedBuilder.build()).build();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        if (event.getReaction().getEmote().getName().equals(DICE)) {
            final Member member = event.getMember();
            if (!rollypolly)
                rolls.putIfAbsent(member, ThreadLocalRandom.current().nextInt(101));
            else {
                rolls.put(member, ThreadLocalRandom.current().nextInt(101));
            }
            event.getTextChannel().editMessageById(getMessageId(), buildScoreBoard()).queue();
        } else if (event.getUser().getIdLong() == getAuthorId() && event.getReactionEmote().getName().equals(END)) {
            TextChannel textChannel = event.getTextChannel();
            if (textChannel != null) {
                //textChannel.removeReactionsById(getMessageId()).queue();
                textChannel.editMessageById(getMessageId(), "Winner is: \uD83C\uDF8A" + rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElseGet(null).getKey().getAsMention() + "\uD83C\uDF8A").queue();
            }
            unregister();
        }
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
