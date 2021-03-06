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

import com.github.breadmoirai.breadbot.plugins.waiter.EventActionFuture;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.samurai.Dispatchable;
import com.github.breadmoirai.samurai.util.IntObjectFunction;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ShutdownEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RollPollMessage implements Dispatchable {

    private static final long serialVersionUID = 777L;

    private static final String DICE = "\uD83C\uDFB2", END = "\uD83C\uDFC1";
    private static final String[] MEDAL = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};
    private static final Color COLOR = new Color(114, 137, 218);

    private final RollPollExtension database;
    private final EventWaiter waiter;
    private final long guildId;
    private final String message;
    private final List<Roll> rolls;
    private final IntObjectFunction<Roll, String> endAction;
    private final Instant endTime;
    private final boolean isContinue;
    private EventActionFuture<Void> shutdownFuture;

    public RollPollMessage(RollPollExtension database, EventWaiter waiter, long guildId, String message,
                           List<Roll> rolls,
                           IntObjectFunction<Roll, String> endAction, Instant endTime, boolean isContinue) {
        this.database = database;
        this.waiter = waiter;
        this.guildId = guildId;
        this.message = message;
        this.rolls = rolls;
        this.endAction = endAction;
        this.endTime = endTime;
        this.isContinue = isContinue;
    }

    public RollPollMessage(RollPollExtension database, EventWaiter waiter, long guildId, String message,
                           List<Roll> rolls, IntObjectFunction<Roll, String> endAction, Instant endTime) {
        this(database, waiter, guildId, message, rolls, endAction, endTime, true);
    }

    public RollPollMessage(RollPollExtension database, EventWaiter waiter, long guildId, String message,
                           IntObjectFunction<Roll, String> endResultProducer, Instant endTime) {
        this(database, waiter, guildId, message, new ArrayList<>(), endResultProducer, endTime, false);
    }

    @Override
    public void dispatch(TextChannel channel) {
        if (isContinue) {
            channel.getHistory().retrievePast(10).queue(messages -> {
                final Optional<Message> first;
                first = messages.stream()
                                .filter(m -> m.getAuthor().getIdLong() == m.getJDA().getSelfUser().getIdLong())
                                .filter(m -> m.getEmbeds().size() == 1)
                                .filter(m -> m.getEmbeds().get(0).getColor().equals(COLOR))
                                .findFirst();
                if (first.isPresent()) {
                    final Message m = first.get();
                    m.editMessage(buildScoreBoard(channel.getGuild())).queue();
                    waitForEvent(m, waiter);
                } else {
                    sendNewMessage(channel);
                }
            });
        } else {
            sendNewMessage(channel);
        }
    }

    private void sendNewMessage(TextChannel channel) {
        channel.sendMessage(
                new MessageBuilder().setContent(message).setEmbed(buildScoreBoard(channel.getGuild())).build())
               .queue(m -> {
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
                  final Roll roll = new Roll(member.getUser().getIdLong());
                  if (!rolls.contains(roll)) {
                      rolls.add(roll);
                  }
                  event.getTextChannel().editMessageById(messageId, buildScoreBoard(event.getGuild())).queue();
              })
              .stopIf((e, i) -> false)
              .waitFor(Instant.now().until(endTime, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS)
              .timeout(() -> {
                  channel.editMessageById(messageId, distDispPoints(channel.getGuild())).queue();
                  channel.clearReactionsById(messageId).queue();
                  shutdownFuture.cancel();
              })
              .build();

        shutdownFuture = waiter.waitFor(ShutdownEvent.class)
                               .action(event -> {
                                   if (database != null)
                                       database.storeRolls(guildId, rolls);
                               })
                               .build();
    }

    private MessageEmbed buildScoreBoard(Guild guild) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("End Time", null);
        embedBuilder.setTimestamp(endTime);
        embedBuilder.setColor(COLOR);
        final StringBuilder description = embedBuilder.getDescriptionBuilder();
        Collections.sort(rolls);
        for (int i = 0; i < rolls.size(); i++) {
            if (i < 3) {
                description.append(MEDAL[i]).append(' ');
            } else {
                description.append(String.format("`%02d.` ", i + 1));
            }
            final Roll roll = rolls.get(i);
            final Member memberById = guild.getMemberById(roll.getMemberId());
            description
                    .append((memberById != null ? memberById.getEffectiveName() : "unknown"))
                    .append(" rolled a **")
                    .append(roll.getRoll())
                    .append("**\n");
        }
        return embedBuilder.build();
    }

    private MessageEmbed distDispPoints(Guild guild) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("Ended at", null);
        embedBuilder.setTimestamp(endTime);
        final ArrayDeque<List<Roll>> rollStack = rolls.stream()
                                                      .collect(Collectors.groupingBy(o -> o.roll))
                                                      .entrySet()
                                                      .stream()
                                                      .sorted(Comparator.<Map.Entry<Integer, List<Roll>>>comparingInt(
                                                              Map.Entry::getKey).reversed())
                                                      .map(Map.Entry::getValue)
                                                      .collect(Collectors.toCollection(ArrayDeque::new));
        StringJoiner sj = new StringJoiner("\n");
        int i = -1;
        while (!rollStack.isEmpty()) {
            final List<Roll> pop = rollStack.pop();
            i += pop.size();
            for (Roll roll : pop) {
                StringBuilder sb = new StringBuilder();
                if (i < 3)
                    sb.append(MEDAL[i]);
                else
                    sb.append(String.format("`%02d.`", i + 1));
                sb.append(' ');
                final Member memberById = guild.getMemberById(roll.getMemberId());
                sb
                        .append((memberById != null ? memberById.getEffectiveName() : "unknown"))
                        .append(" rolled a **")
                        .append(roll.getRoll())
                        .append("** ")
                        .append(endAction.apply(i, roll));
                sj.add(sb.toString());
            }
        }
        embedBuilder.setDescription(sj.toString());
        return embedBuilder.build();
    }

    public static class Roll implements Comparable<Roll> {

        private final long memberId;
        private final int roll;

        private Roll(long memberId) {
            this.memberId = memberId;
            this.roll = ThreadLocalRandom.current().nextInt(101);
        }

        public Roll(long memberId, int roll) {
            this.memberId = memberId;
            this.roll = roll;
        }

        public long getMemberId() {
            return memberId;
        }

        public int getRoll() {
            return roll;
        }

        @Override
        public int compareTo(@NotNull Roll o) {
            return o.getRoll() - getRoll();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Roll roll = (Roll) o;
            return getMemberId() == roll.getMemberId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMemberId());
        }
    }

}
