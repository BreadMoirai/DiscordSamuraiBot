package samurai.messages.impl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.command.fun.Roll;
import samurai.messages.MessageManager;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ReactionListener;
import samurai.points.PointTracker;

import java.nio.ByteBuffer;
import java.time.Instant;
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
    private transient final int time;
    private transient TimeUnit unit;
    private OffsetDateTime endTime;
    private int pointValue;
    private transient PointTracker pointTracker;

    {
        rolls = new HashMap<>();
        endTime = null;
    }

    public RollPoll(int time, TimeUnit unit, int pointValue, PointTracker pointTracker) {
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
                final Map.Entry<Member, Integer> memberIntegerEntry = rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElseGet(null);
                if (memberIntegerEntry == null) {
                    message1.editMessage("No Winner...").queue();
                    unregister();
                    return;
                }
                Member winner = memberIntegerEntry.getKey();
                if (pointTracker != null && pointValue > 0) {
                    message1.editMessage(new MessageBuilder().append("The Winner is... \uD83C\uDF8A").append(winner.getAsMention()).append("\uD83C\uDF8A").setEmbed(distDispPoints(pointValue)).build()).queue();
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
            description.append(memberIntegerEntry.getKey().getEffectiveName()).append(" rolled a **").append(memberIntegerEntry.getValue())
                    .append("**\n");
        });
        final MessageBuilder mb = new MessageBuilder().append("Click ").append(DICE).append(" to roll");
        if (pointTracker != null && pointValue > 0) {
            mb.append(" for a maximum prize of **").append(String.valueOf(pointValue)).append("**");
        }
        mb.append('!');
        return mb.setEmbed(embedBuilder.build()).build();
    }

    private MessageEmbed distDispPoints(int value) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        if (endTime != null) {
            embedBuilder.setTimestamp(endTime);
        }
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        final long guildId = getGuildId();
        int i = 1;
        List<Map.Entry<Member, Integer>> toSort = new ArrayList<>();
        toSort.addAll(rolls.entrySet());
        toSort.sort(Comparator.comparingInt((ToIntFunction<Map.Entry<Member, Integer>>) Map.Entry::getValue).reversed());
        int previous = toSort.get(0).getValue();
        for (Map.Entry<Member, Integer> memberRoll : toSort) {
            int pos = i;
            value *= (double)memberRoll.getValue() / (double)previous;
            pointTracker.offsetPoints(guildId, memberRoll.getKey().getUser().getIdLong(), value);

            if (pos <= 3) description.append(MEDAL[pos - 1]).append(' ');
            else description.append(String.format("`%02d.`", pos));

            description.append(memberRoll.getKey().getEffectiveName())
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
            rolls.putIfAbsent(member, ThreadLocalRandom.current().nextInt(101));

            event.getTextChannel().editMessageById(getMessageId(), buildScoreBoard()).queue();
        } else if (event.getUser().getIdLong() == getAuthorId() && event.getReactionEmote().getName().equals(END)) {
            TextChannel textChannel = event.getTextChannel();
            if (textChannel != null) {
                textChannel.clearReactionsById(getMessageId()).queue();
                textChannel.editMessageById(getMessageId(), "Winner is: \uD83C\uDF8A" + rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElseGet(null).getKey().getAsMention() + "\uD83C\uDF8A").queue();
            }
            unregister();
        }
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public byte[] download() {
        int memberSize = rolls.size() * (Long.BYTES + Integer.BYTES);
        final ByteBuffer bb = ByteBuffer.allocate((Long.BYTES * 4) + (Integer.BYTES * 2) + memberSize);
        bb.putLong(getMessageId());
        bb.putLong(getGuildId());
        bb.putLong(getChannelId());
        bb.putInt(pointValue);
        bb.putLong(endTime.toEpochSecond());
        bb.putInt(rolls.size());
        for (Map.Entry<Member, Integer> memberRoll : rolls.entrySet()) {
            bb.putLong(memberRoll.getKey().getUser().getIdLong());
            bb.putInt(memberRoll.getValue());
        }
        return bb.array();
    }

    public static boolean upload(byte[] bytes, MessageManager mm, PointTracker pp) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        long messageId = bb.getLong();
        long guildId = bb.getLong();
        long channelId = bb.getLong();
        int points = bb.getInt();
        long end = bb.getLong();
        int size = bb.getInt();
        final long l = end - Instant.now().getEpochSecond();
        if (l < 0) return false;
        final RollPoll rollPoll = new RollPoll(Math.toIntExact(l), TimeUnit.SECONDS, points, null);
        rollPoll.setChannelId(channelId);
        rollPoll.setGuildId(guildId);
        final Guild guild = mm.getClient().getGuildById(guildId);
        for (int i = 0; i < size; i++) {
            rollPoll.rolls.put(guild.getMemberById(bb.getLong()), bb.getInt());
        }
        rollPoll.pointTracker = pp;
        rollPoll.replace(mm, messageId);
        return true;
    }
}
