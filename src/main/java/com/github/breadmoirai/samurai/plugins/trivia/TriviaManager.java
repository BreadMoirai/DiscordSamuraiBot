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
import com.github.breadmoirai.samurai.plugins.personal.BreadMoiraiSamuraiPlugin;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TriviaManager {

    private static final String SKIP = "\u23ed";
    private static double VALUE = .007;
    private static int SKIP_THRESHOLD = 3;
    //    private static final int WRONG_COOLDOWN = 5;
    private final Emote minusOne;
    private final TextChannel channel;
    private final EventWaiter waiter;
    private final TriviaPlugin trivia;
    private final DerbyPointPlugin points;

    private TriviaSession session;
    private Instant nextQuizTime;
    private ScheduledFuture<?> futureQuiz;
    private TLongSet skips;
    private Set<EventActionFuture<Void>> reactionFutures;
//    private TLongObjectMap<Instant> wrongAnswers;

    public TriviaManager(TextChannel channel, EventWaiter waiter,
                         TriviaPlugin trivia, DerbyPointPlugin points) {
        this.channel = channel;
        this.waiter = waiter;
        this.trivia = trivia;
        this.points = points;
        this.minusOne = BreadMoiraiSamuraiPlugin.minusOne;
        this.reactionFutures = new HashSet<>();
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

    public long getChannelId() {
        return channel.getIdLong();
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
        reactionFutures.add(reaction.build());
    }

    private MessageEmbed getNext() {
        skips = new TLongHashSet();
        if (reactionFutures != null)
            reactionFutures.forEach(EventActionFuture::cancel);
        reactionFutures = new HashSet<>();
        return session.nextQuestion();
    }

    private void endSession() {
        session = null;
        if (reactionFutures != null) {
            reactionFutures.forEach(EventActionFuture::cancel);
            reactionFutures = null;
        }
        skips = null;
        nextQuizTime = Instant.now().plus(Duration.ofHours(3));
        trivia.getService().schedule(this::dispatchTrivia, 3, TimeUnit.HOURS);
        repeatQuestion();
    }

    public void repeatQuestion() {
        if (session != null) {
            channel.sendMessage(session.getAnswer(true)).queue(this::waitForSkip);
        } else {
            channel.sendMessage(new EmbedBuilder().setTitle("Next Trivia Session").setTimestamp(nextQuizTime).build())
                   .queue();
        }
    }

    public void answer(String s, Message m, Member author) {
        if (session.checkAnswer(s)) {
            channel.sendMessage(new MessageBuilder().append(author)
                                                    .appendFormat(" answered correctly and gains %.2f points", VALUE)
                                                    .setEmbed(session.getAnswer(true))
                                                    .build()).queue();
            points.offsetPoints(author.getUser().getIdLong(), VALUE);
            if (session.hasNext())
                channel.sendMessage(getNext()).queue(this::waitForSkip);
            else
                endSession();
        } else {
            m.addReaction(minusOne).queue();
        }
    }

    public void shutdown() {
        futureQuiz.cancel(true);
        if (reactionFutures != null) {
            reactionFutures.forEach(EventActionFuture::cancel);
        }
        trivia.setNextTime(getGuildId(), nextQuizTime);
    }

    public boolean isActive() {
        return session != null;
    }
}
