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

import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
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
    private final ScheduledExecutorService terminationExecutor;

    public MusicPlugin(String youtubeKey) {
        youtube = new YoutubeAPI(youtubeKey);
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        audioManagers = new ConcurrentHashMap<>();
        terminationExecutor = Executors.newSingleThreadScheduledExecutor();
        terminationTask = new ConcurrentHashMap<>();
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
            final ScheduledFuture<?> scheduledFuture = terminationExecutor.schedule(() -> {
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

    public void close() {
        terminationTask.clear();
        terminationExecutor.shutdown();
        audioManagers.forEachValue(1000L, GuildAudioManager::destroy);
        audioManagers.clear();
        playerManager.shutdown();
    }

    @Override
    public void initialize(BreadBotBuilder builder) {

    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ShutdownEvent) {
            close();
        }
    }
}
