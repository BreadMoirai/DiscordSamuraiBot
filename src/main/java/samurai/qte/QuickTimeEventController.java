/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
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
 *
 */
package samurai.qte;

import com.typesafe.config.ConfigFactory;
import samurai.command.CommandScheduler;
import samurai.messages.MessageManager;
import samurai.qte.service.jeopardy.JeopardyService;
import samurai.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class QuickTimeEventController {

    private static final int COOLDOWN = 5;

    private static final long TARGET_CHANNEL;

    private final List<QuickTimeEventService> services;
    private final MessageManager messageManager;
    private transient Instant lastQuizTime;
    private transient ScheduledFuture<?> future;

    static {
        TARGET_CHANNEL = ConfigFactory.load("items").getLong("quiz");
    }


    public QuickTimeEventController(MessageManager messageManager) {
        services = new ArrayList<>();
        services.add(new JeopardyService());
        this.messageManager = messageManager;
    }


    void onCompletion() {
        if (((future != null) && future.cancel(false)) || (future == null))
            if (lastQuizTime == null || ChronoUnit.HOURS.between(lastQuizTime, Instant.now()) >= COOLDOWN) {
                sendNewQuiz();
            } else {
                final Instant delay = lastQuizTime.plus(COOLDOWN, ChronoUnit.HOURS);
                future = CommandScheduler.getCommandExecutor().schedule(this::sendNewQuiz, Instant.now().until(delay, ChronoUnit.SECONDS), TimeUnit.SECONDS);
                messageManager.getClient().getTextChannelById(TARGET_CHANNEL).sendMessage("Next QuickTimeEvent will be available in " + TimeUtil.format(Duration.between(Instant.now(), delay), ChronoUnit.MINUTES)).queue();
            }
    }

    void sendNewQuiz() {
        future = null;
        lastQuizTime = Instant.now();
        final QuickTimeMessage provide = services.get(ThreadLocalRandom.current().nextInt(services.size())).provide();
        provide.setQuickTimeEventController(this);
        provide.setChannelId(TARGET_CHANNEL);
        messageManager.submit(provide);
    }

    public Duration getCoolDown() {
        if (future != null) {
           return Duration.of(future.getDelay(TimeUnit.MILLISECONDS), ChronoUnit.MILLIS);
        } else return Duration.ZERO;
    }

    public void onReady(Instant creationTime) {
        this.lastQuizTime = creationTime;
        if (lastQuizTime == null)
            onCompletion();
    }
}
