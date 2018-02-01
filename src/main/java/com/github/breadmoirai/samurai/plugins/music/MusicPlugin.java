/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.breadmoirai.samurai.plugins.music;

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.breadbot.framework.error.BreadBotException;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiterPlugin;
import com.github.breadmoirai.samurai.Dispatchable;
import com.github.breadmoirai.samurai.plugins.music.commands.AutoPlay;
import com.github.breadmoirai.samurai.plugins.music.commands.CanPlay;
import com.github.breadmoirai.samurai.plugins.music.commands.History;
import com.github.breadmoirai.samurai.plugins.music.commands.Join;
import com.github.breadmoirai.samurai.plugins.music.commands.Leave;
import com.github.breadmoirai.samurai.plugins.music.commands.MusicHelp;
import com.github.breadmoirai.samurai.plugins.music.commands.Pause;
import com.github.breadmoirai.samurai.plugins.music.commands.Play;
import com.github.breadmoirai.samurai.plugins.music.commands.Previous;
import com.github.breadmoirai.samurai.plugins.music.commands.Related;
import com.github.breadmoirai.samurai.plugins.music.commands.Repeat;
import com.github.breadmoirai.samurai.plugins.music.commands.Shuffle;
import com.github.breadmoirai.samurai.plugins.music.commands.Skip;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class MusicPlugin implements CommandPlugin, EventListener {

    private final YoutubeAPI youtube;

    private final AudioPlayerManager playerManager;
    private final ConcurrentHashMap<Long, GuildAudioManager> audioManagers;
    private final ConcurrentHashMap<Long, ScheduledFuture> terminationTask;
    private final ScheduledExecutorService executor;

    private DispatchableDispatcher handler;

    public MusicPlugin(String youtubeKey) {
        youtube = new YoutubeAPI(youtubeKey);
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        audioManagers = new ConcurrentHashMap<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        terminationTask = new ConcurrentHashMap<>();
    }

    @Override
    public void initialize(BreadBotBuilder builder) {
        builder.addCommand(new AutoPlay())
                .addCommand(new CanPlay())
                .addCommand(new History())
                .addCommand(new Join())
                .addCommand(new Leave())
                .addCommand(new Pause())
                .addCommand(new Play())
                .addCommand(new Previous())
                .addCommand(new Related())
                .addCommand(new Repeat())
                .addCommand(new Shuffle())
                .addCommand(new Skip())
                .addCommand(event -> event.reply("Sorry, but I'm too poor for this. My free amazon AWS trial is over and changing volume is the single most computationally expensive part of this bot."), command -> command.setKeys("volume", "vol"))
                .addCommand(new MusicHelp());
    }

    @Override
    public void onBreadReady(BreadBot client) {
        if (!client.hasPlugin(EventWaiterPlugin.class)) {
            throw new BreadBotException("The MusicPlugin requires an EventWaiterPlugin");
        }
        if (client.getResultManager().getResultHandler(Dispatchable.class) == null) {
            throw new BreadBotException("The MusicPlugin requires a DispatchableDispatcher");
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ShutdownEvent) {
            close();
        }
    }

    public List<String> getRelated(String videoID, long size) {
        return youtube.getRelated(videoID, size);
    }

    public boolean openConnection(VoiceChannel channel) {
        final long idLong = channel.getGuild().getIdLong();
        if (!audioManagers.containsKey(idLong)) {
            audioManagers.put(idLong, new GuildAudioManager(playerManager, channel.getGuild().getAudioManager(), this));
        }
        return audioManagers.get(idLong).openAudioConnection(channel);
    }

    public void loadItem(Object orderingKey, String request, AudioLoadResultHandler resultHandler) {
        if (orderingKey instanceof GuildAudioManager)
            orderingKey = ((GuildAudioManager) orderingKey).scheduler;
        playerManager.loadItemOrdered(orderingKey, request, resultHandler);
    }

    public Optional<GuildAudioManager> retrieveManager(long guildId) {
        return Optional.ofNullable(audioManagers.get(guildId));
    }

    public Optional<GuildAudioManager> removeManager(long guildId) {
        return Optional.ofNullable(audioManagers.remove(guildId));
    }

    public void scheduleLeave(long idLong) {
        final Optional<GuildAudioManager> managerOptional = retrieveManager(idLong);
        if (managerOptional.isPresent()) {
            if (managerOptional.get().player.isPaused()) {
                return;
            }
            final ScheduledFuture<?> scheduledFuture = executor.schedule(() -> {
                removeManager(idLong).ifPresent(GuildAudioManager::destroy);
                terminationTask.remove(idLong);
            }, 5, TimeUnit.MINUTES);
            terminationTask.put(idLong, scheduledFuture);
        }
    }

    public void cancelLeave(long idLong) {
        final ScheduledFuture leaveFuture = terminationTask.get(idLong);
        if (leaveFuture != null) {
            terminationTask.remove(idLong);
            leaveFuture.cancel(false);
        }
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void close() {
        terminationTask.clear();
        executor.shutdown();
        audioManagers.forEachValue(1000L, GuildAudioManager::destroy);
        audioManagers.clear();
        playerManager.shutdown();
    }
}
