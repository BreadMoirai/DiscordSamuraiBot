package samurai.messages.impl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.listeners.ReactionListener;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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

    {
        rolls = new HashMap<>(50);
        endTime = null;
    }

    public RollPoll(int time, TimeUnit unit, boolean rollypolly) {
        this.time = time;
        this.unit = unit;
        this.rollypolly = rollypolly;
    }

    public RollPoll() {
        this.time = 0;
        this.unit = null;
        rollypolly = false;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Click " + DICE + " to roll!\n").build();
    }

    @Override
    protected void onReady(Message message) {
        message.addReaction(DICE).queue();
        if (time > 0) {
            message.getChannel().getMessageById(getMessageId()).queueAfter(time, unit, message1 -> {
                message1.editMessage("Winner is: \uD83C\uDF8A" + rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElseGet(null).getKey().getAsMention() + "\uD83C\uDF8A").queue();
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
                description.append('`').append(pos).append(".` ");
            }
            description.append(memberIntegerEntry.getKey().getEffectiveName()).append(" rolled a ").append(memberIntegerEntry.getValue())
                    .append('\n');
        });
        return new MessageBuilder().append("Click " + DICE + " to roll!\n").setEmbed(embedBuilder.build()).build();
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
