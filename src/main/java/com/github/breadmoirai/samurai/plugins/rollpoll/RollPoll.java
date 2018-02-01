/*
 *     Copyright 2017-2018 Ton Ly
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.breadmoirai.samurai.plugins.rollpoll;

import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.samurai.Dispatchable;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class RollPoll implements Dispatchable {

    private static final long serialVersionUID = 777L;

    private static final String DICE = "\uD83C\uDFB2", END = "\uD83C\uDFC1";
    private static final String[] MEDAL = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};

    private final Map<Long, Integer> rolls;
    private final String message;
    private final Function<RollPoll, Stack<String>> endAction;
    private final Instant endTime;

    public RollPoll(String message, Function<RollPoll, Stack<String>> endResultProducer, Instant endTime) {
        this.rolls = new HashMap<>();
        this.message = message;
        this.endAction = endResultProducer;
        this.endTime = endTime;
    }

    @Override
    public void dispatch(CommandEvent event, EventWaiter waiter, MessageChannel channel) {
        event.reply(message)
                .setEmbed(buildScoreBoard(event.getGuild()))
                .onSuccess(m -> {
                    m.addReaction(DICE).queue();
                    waitForEvent(m, waiter);
                });
    }

    private void waitForEvent(Message m, EventWaiter waiter) {
        final TextChannel channel = m.getTextChannel();
        final long messageId = m.getIdLong();
        waiter.waitForReaction()
                .withName(DICE)
                .in(channel)
                .onMessages(messageId)
                .action(event -> {
                    final Member member = event.getMember();
                    rolls.putIfAbsent(member.getUser().getIdLong(), ThreadLocalRandom.current().nextInt(101));
                    event.getTextChannel().editMessageById(messageId, buildScoreBoard(event.getGuild())).queue();
                })
                .waitFor(Instant.now().until(endTime, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS)
                .timeout(() -> {
                    channel.editMessageById(messageId, distDispPoints(channel.getGuild())).queue();
                    channel.clearReactionsById(messageId).queue();
                });
    }
//
//    @Override
//    protected void onReady(Message message) {
//        message.addReaction(DICE).queue();
//        message.getChannel().getMessageById(getMessageId()).queueAfter(time, unit, message1 -> {
//            final Map.Entry<Long, Integer> memberIntegerEntry = rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElse(null);
////                if (pointTracker != null && pointValue > 0) {
////                    final Member memberById = message1.getGuild().getMemberById(winner);
////                    message1.editMessage(new MessageBuilder().append("The Winner is... \uD83C\uDF8A").append((memberById != null ? memberById.getAsMention() : "unknown")).append("\uD83C\uDF8A").setEmbed(distDispPoints(pointValue, message1.getGuild())).build()).queue();
////                } else {
////                    final Member memberById = message1.getGuild().getMemberById(winner);
////                    message1.editMessage("Winner is \uD83C\uDF8A" + (memberById != null ? memberById.getAsMention() : "unknown") + "\uD83C\uDF8A").queue();
////                }
//            message1.clearReactions().queue();
//        });
//    }
//
//}

    private MessageEmbed buildScoreBoard(Guild guild) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("End Time", null);
        embedBuilder.setTimestamp(endTime);
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        AtomicInteger i = new AtomicInteger(1);
        rolls.entrySet().stream().sorted(Comparator.comparingInt((ToIntFunction<Map.Entry<Long, Integer>>) Map.Entry::getValue).reversed()).forEachOrdered(memberIntegerEntry -> {
            final int pos = i.getAndIncrement();
            if (pos <= 3) {
                description.append(MEDAL[pos - 1]).append(' ');
            } else {
                description.append(String.format("`%02d.` ", pos));
            }
            final Member memberById = guild.getMemberById(memberIntegerEntry.getKey());
            description
                    .append((memberById != null ? memberById.getEffectiveName() : "unknown"))
                    .append(" rolled a **")
                    .append(memberIntegerEntry.getValue())
                    .append("**\n");
        });
        return embedBuilder.build();
    }

    private MessageEmbed distDispPoints(Guild guild) {
        final Stack<String> apply = endAction.apply(this);
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("Ended at", null);
        embedBuilder.setTimestamp(endTime);
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        List<Map.Entry<Long, Integer>> toSort = new ArrayList<>(rolls.entrySet());
        toSort.sort(Comparator.comparingInt((ToIntFunction<Map.Entry<Long, Integer>>) Map.Entry::getValue).reversed());
        for (int i = 0; i < toSort.size(); i++) {
            if (i < 3)
                description.append(MEDAL[i]);
            else
                description.append(String.format("`%02d.`", i));
            description.append(' ');
            final Map.Entry<Long, Integer> memberRoll = toSort.get(i);
            final Member memberById = guild.getMemberById(memberRoll.getKey());
            description.append((memberById != null ? memberById.getEffectiveName() : "unknown"))
                    .append(" rolled a **").append(memberRoll.getValue())
                    .append("** ");
            description.append(apply.pop());
            if (i != toSort.size() - 1)
                description.append('\n');
        }
        return embedBuilder.build();
    }

//    @Override
//    public void onReaction(MessageReactionAddEvent event) {
//        if (event.getReaction().getEmote().getName().equals(DICE)) {
//
//        } else if (event.getUser().getIdLong() == getAuthorId() && event.getReactionEmote().getName().equals(END)) {
//            final Map.Entry<Long, Integer> memberIntegerEntry = rolls.entrySet().stream().max(Comparator.comparingInt(Map.Entry::<Integer>getValue)).orElse(null);
//            TextChannel textChannel = event.getTextChannel();
//            if (textChannel != null) {
//                if (memberIntegerEntry == null) {
//                    textChannel.editMessageById(getMessageId(), "No Winner...").queue();
//                    textChannel.clearReactionsById(getMessageId()).queue();
//                    unregister();
//                    return;
//                }
//                long winner = memberIntegerEntry.getKey();
//                final Guild guild = textChannel.getGuild();
//                if (pointTracker != null && pointValue > 0) {
//                    final Member memberById = guild.getMemberById(winner);
//                    textChannel.editMessageById(getMessageId(), new MessageBuilder().append("The Winner is... \uD83C\uDF8A").append((memberById != null ? memberById.getAsMention() : "unknown")).append("\uD83C\uDF8A").setEmbed(distDispPoints(pointValue, guild)).build()).queue();
//                } else {
//                    final Member memberById = guild.getMemberById(winner);
//                    textChannel.editMessageById(getMessageId(), "Winner is \uD83C\uDF8A" + (memberById != null ? memberById.getAsMention() : "unknown") + "\uD83C\uDF8A").queue();
//                }
//                textChannel.clearReactionsById(getMessageId()).queue();
//                unregister();
//            }
//            unregister();
//        }
//    }
}
