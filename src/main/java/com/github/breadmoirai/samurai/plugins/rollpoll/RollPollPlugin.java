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

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.breadbot.framework.error.BreadBotException;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiterPlugin;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.derby.MissingDerbyPluginException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.OptionalLong;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RollPollPlugin implements CommandPlugin, net.dv8tion.jda.core.hooks.EventListener {

    private RollPollExtension database;

    private final ScheduledExecutorService service;
    private JDA jda;
    private EventWaiter waiter;

    public RollPollPlugin(ScheduledExecutorService service) {
        this.service = service;
    }

    @Override
    public void initialize(BreadBotBuilder builder) {
        final DerbyDatabase database = builder.getPlugin(DerbyDatabase.class);
        this.database = database.getExtension(RollPollExtension::new);
    }

    @Override
    public void onBreadReady(BreadBot client) {
        if (!client.hasPlugin(DerbyDatabase.class)) {
            throw new MissingDerbyPluginException();
        }
        if (!client.hasPlugin(EventWaiterPlugin.class)) {
            throw new BreadBotException("The RollPollPlugin requires an EventWaiterPlugin");
        }
        waiter = client.getPlugin(EventWaiterPlugin.class).getEventWaiter();

    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            onReadyEvent(((ReadyEvent) event));
        } else if (event instanceof ShutdownEvent) {
            onShutdownEvent(((ShutdownEvent) event));
        }
    }

    private void onReadyEvent(ReadyEvent event) {
        jda = event.getJDA();
        service.scheduleAtFixedRate(this::dispatchRollPolls, LocalTime.now().until(LocalTime.MIDNIGHT, ChronoUnit.MILLIS), ChronoUnit.DAYS.getDuration().toMillis(), TimeUnit.MILLISECONDS);
    }

    private void dispatchRollPolls() {
        database.getRollPollChannels().forEachEntry((a, b) -> {
            final TextChannel textChannelById = jda.getTextChannelById(b);
            if (textChannelById != null) {
                final RollPollMessage rollPoll = new RollPollMessage(waiter, "Roll Today!", (i, roll) -> "wow", Instant.from(LocalDateTime.of(LocalDate.now(), LocalTime.MAX)));
                rollPoll.dispatch(textChannelById);
            }
            return true;
        });
    }

    private void onShutdownEvent(ShutdownEvent event) {

    }

    public OptionalLong getDesignatedChannel(long guildId) {
        return database.selectRollPollChannel(guildId);
    }

    public void setDesignatedChannel(long guildId, long channelId) {
        if (getDesignatedChannel(guildId).isPresent()) {
            database.updateRollPollChannel(guildId, channelId);
        } else {
            database.insertRollPollChannel(guildId, channelId);
        }
    }

    public void deleteDesignatedChannel(long guildId) {
        database.deleteRollPollChannel(guildId);
    }

}
