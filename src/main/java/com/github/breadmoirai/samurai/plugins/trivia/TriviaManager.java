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

package com.github.breadmoirai.samurai.plugins.trivia;

import com.github.breadmoirai.breadbot.plugins.waiter.EventActionFuture;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.breadbot.plugins.waiter.ReactionEventActionBuilder;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TriviaManager {

    private static final String SKIP = "\u23ed";
    private static final int SKIP_THRESHOLD = 1;
    private static final int WRONG_COOLDOWN = 5;
    private final TextChannel channel;
    private final EventWaiter waiter;
    private final TriviaPlugin trivia;
    private final DerbyPointPlugin points;

    private TriviaSession session;
    private Instant nextQuizTime;
    private ScheduledFuture<?> futureQuiz;
    private TLongSet skips;
    private EventActionFuture<Void> reactionFuture;
//    private TLongObjectMap<Instant> wrongAnswers;

    public TriviaManager(TextChannel channel, EventWaiter waiter,
                         TriviaPlugin trivia, DerbyPointPlugin points) {
        this.channel = channel;
        this.waiter = waiter;
        this.trivia = trivia;
        this.points = points;
        setup();
    }

    private void setup() {
        nextQuizTime = trivia.getNextTime(getGuildId());
        if (nextQuizTime.isBefore(Instant.now())) {
            dispatchTrivia();
        } else {
            futureQuiz = trivia.getService()
                               .schedule(this::dispatchTrivia, Instant.now().until(nextQuizTime, ChronoUnit.MILLIS),
                                         TimeUnit.MILLISECONDS);
        }
    }

    public long getGuildId() {
        return channel.getGuild().getIdLong();
    }

    private void dispatchTrivia() {
        session = trivia.getRandomProvider().provide();
        session.setIcon(channel.getJDA().getSelfUser().getAvatarUrl());

        channel.sendMessage(getNext()).queue(this::waitForSkip);
    }

    private MessageEmbed getNext() {
        skips = new TLongHashSet();
        if (reactionFuture != null)
            reactionFuture.cancel();
        return session.nextQuestion();
    }

    private void waitForSkip(Message message) {
        message.addReaction(SKIP).queue();
        ReactionEventActionBuilder<Void> reaction = waiter.waitForReaction();
        reaction = reaction.on(message)
                           .withName(SKIP)
                           .matching(event -> !event.getUser().isBot())
                           .matching(event -> !skips.contains(event.getUser().getIdLong()))
                           .action(event -> skips.add(event.getUser().getIdLong()))
                           .stopIf((e, i) -> i >= SKIP_THRESHOLD)
                           .finish(() -> {
                               channel.sendMessage(session.getAnswer(false)).queue();
                               if (session.hasNext())
                                   channel.sendMessage(getNext()).queue(this::waitForSkip);
                               else
                                   endSession();
                           });
        reactionFuture = reaction.build();
    }

    private void endSession() {
        session = null;
        if (reactionFuture != null) {
            reactionFuture.cancel();
            reactionFuture = null;
        }
        skips = null;
        nextQuizTime = Instant.now().plus(Duration.ofHours(3));
        trivia.getService().schedule(this::dispatchTrivia, 3, TimeUnit.HOURS);
    }

    public void shutdown() {
        futureQuiz.cancel(true);
        if (reactionFuture != null) {
            reactionFuture.cancel();
        }
        trivia.setNextTime(getGuildId(), nextQuizTime);
    }

    public void answer(String s, Message m, Member author) {
        if (session.checkAnswer(s)) {
            channel.sendMessage(new MessageBuilder().append(author)
                                                    .append(" answered correctly and gains .04 points")
                                                    .setEmbed(new EmbedBuilder(session.getAnswer(true)).build())
                                                    .build()).queue();
            points.offsetPoints(author.getUser().getIdLong(), .04);
            if (session.hasNext())
                channel.sendMessage(getNext()).queue(this::waitForSkip);
            else
                endSession();
        } else {

        }
    }
}
