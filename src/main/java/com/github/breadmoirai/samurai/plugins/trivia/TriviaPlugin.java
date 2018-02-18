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

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Author;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Content;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.breadbot.plugins.admin.Admin;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiterPlugin;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import com.github.breadmoirai.samurai.plugins.trivia.triviaquestionsdotnet.TriviaQuestionsDotNetProvider;
import com.sedmelluq.discord.lavaplayer.tools.ExecutorTools;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TriviaPlugin implements CommandPlugin {

    private final List<TriviaProvider> providers;
    private final ScheduledExecutorService service;
    private final TLongObjectMap<TriviaManager> managers;
    private TriviaChannelDatabase database;
    private JDA jda;
    private EventWaiter waiter;
    private DerbyPointPlugin points;

    public TriviaPlugin() {
        this.providers = new ArrayList<>();
        this.service = Executors.newSingleThreadScheduledExecutor();
        managers = new TLongObjectHashMap<>();
    }

    @Override
    public void initialize(BreadBotBuilder builder) {
        builder.addCommand(this);
    }

    @Override
    public void onBreadReady(BreadBot client) {
        waiter = client.getPlugin(EventWaiterPlugin.class).getEventWaiter();
        final DerbyDatabase databasePlugin = client.getPlugin(DerbyDatabase.class);
        points = client.getPlugin(DerbyPointPlugin.class);

        database = databasePlugin.getExtension(TriviaChannelDatabase::new);
        registerProvider(new TriviaQuestionsDotNetProvider(databasePlugin));

        waiter.waitFor(ReadyEvent.class).action(this::onReady);
        waiter.waitFor(ShutdownEvent.class).action(event -> ExecutorTools.shutdownExecutor(service, "Trivia Service"));
    }

    public void registerProvider(TriviaProvider provider) {
        providers.add(provider);
    }

    private void onReady(ReadyEvent event) {
        jda = event.getJDA();

        database.getTriviaChannels().forEachEntry((a, b) -> {
            final TextChannel channel = jda.getTextChannelById(b);
            if (channel == null) {
                return true;
            }
            managers.put(a, new TriviaManager(channel, waiter, this, points));
            return true;
        });
    }

    public ScheduledExecutorService getService() {
        return service;
    }

    public TriviaProvider getRandomProvider() {
        return providers.get(0);
    }

    public void setNextTime(long guildId, Instant time) {
        database.setNextTime(guildId, time);
    }

    public Instant getNextTime(long guildId) {
        return database.getNextTime(guildId);
    }

    @Admin
    @Command
    public String enableTrivia(TextChannel channel) {
        final TriviaManager triviaManager = managers.get(channel.getGuild().getIdLong());
        if (triviaManager == null || triviaManager.getChannelId() != channel.getIdLong())
            managers.put(channel.getGuild().getIdLong(), new TriviaManager(channel, waiter, this, points));
        database.setTriviaChannel(channel.getGuild().getIdLong(), channel.getIdLong());
        return "Trivia has been enabled in this channel";
    }

    @Command
    public String disableTrivia(Guild guild) {
        final TriviaManager manager = managers.remove(guild.getIdLong());
        if (manager != null) {
            manager.shutdown();
        }
        database.removeTriviaChannel(guild.getIdLong());
        return "Trivia has been disabled on this server";
    }

    @Command
    public void answer(Guild guild, Message m, @Content String ans, @Author Member author) {
        if (ans == null)
            return;
        final TriviaManager triviaManager = managers.get(guild.getIdLong());
        if (triviaManager == null) {
            m.getTextChannel().sendMessage("Trivia is not enabled").queue();
            return;
        }
        if (!triviaManager.isActive()) {
            question(m.getTextChannel(), guild);
            return;
        }
        String s = ans.toLowerCase()
                      .replaceAll("\\s+", " ")
                      .replaceAll(",\\s*", " ")
                      .replaceAll("&", "and");
        if (s.startsWith("the")) {
            s = s.substring(4);
        }
        triviaManager.answer(s, m, author);
    }

    @Command
    public void question(TextChannel channel, Guild guild) {
        final TriviaManager triviaManager = managers.get(guild.getIdLong());
        if (triviaManager == null) {
            channel.sendMessage("Trivia is not enabled").queue();
        } else if (channel.getIdLong() == triviaManager.getChannelId()) {
            triviaManager.repeatQuestion();
        }
    }

}
