/*
 *       Copyright 2017 Ton Ly
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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;
import samurai.command.CommandScheduler;
import samurai.command.manage.Schedule;
import samurai.messages.MessageManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class QuickTimeEventController {

    public static final int COOLDOWN = 4;
    private final long targetChannel;

    private final List<QuickTimeEventService> services;
    private final MessageManager messageManager;
    private transient Instant lastQuizTime;
    private transient ScheduledFuture<?> future;

    {
        targetChannel = ConfigFactory.load("items").getLong("quiz");
    }


    public QuickTimeEventController(MessageManager messageManager, Instant lastQuizTime) {
        services = new ArrayList<>();
        services.add(new JeopardyService());
        this.messageManager = messageManager;
        this.lastQuizTime = lastQuizTime;
        onCompletion();
    }


    void onCompletion() {
        if (future != null && future.cancel(false))
            if (lastQuizTime == null || ChronoUnit.HOURS.between(lastQuizTime, Instant.now()) >= COOLDOWN) {
                sendNewQuiz();
            } else {
                future = CommandScheduler.getCommandExecutor().schedule(this::sendNewQuiz, ChronoUnit.SECONDS.between(Instant.now(), lastQuizTime.plus(COOLDOWN, ChronoUnit.HOURS)), TimeUnit.SECONDS);
            }
    }
    

    private void sendNewQuiz() {
        lastQuizTime = Instant.now();
        final QuizMessage provide = services.get(ThreadLocalRandom.current().nextInt(services.size())).provide();
        provide.setQteController(this);
        provide.setChannelId(targetChannel);
        messageManager.submit(provide);
    }
}
