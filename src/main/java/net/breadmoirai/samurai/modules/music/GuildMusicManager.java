/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;


/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {

    private final AudioManager audioManager;
    private final MusicModule module;
    private int volume;

    /**
     * Audio player for the guild.
     */
    private AudioPlayer player;

    /**
     * Track scheduler for the player.
     */
    private final TrackScheduler scheduler;


    GuildMusicManager(MusicModule module, AudioManager audiomanager, int volume) {
        this.module = module;
        this.player = module.createPlayer();
        this.volume = volume;
        this.player.setVolume(volume);
        this.scheduler = new TrackScheduler(player, module);
        this.player.addListener(scheduler);
        this.audioManager = audiomanager;
        audiomanager.setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public boolean isConnected() {
        return audioManager.isConnected();
    }


    public VoiceChannel getConnectedChannel() {
        return audioManager.getConnectedChannel();
    }

    public void openConnection(VoiceChannel channel) {
        audioManager.openAudioConnection(channel);
    }

    public void closeConnection() {
        audioManager.closeAudioConnection();
    }

    public AudioManager getManager() {
        return audioManager;
    }

    public void setVolume(int newVol) {
        this.volume = newVol;
        player.setVolume(newVol);
    }

    public int getVolume() {
        return volume;
    }

    AudioPlayer createNewPlayer() {
        this.player = module.createPlayer();
        player.setVolume(volume);
        player.addListener(scheduler);
        return player;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public void loadItem(String request, AudioLoadResultHandler trackLoader) {
        module.loadItem(this, request, trackLoader);
    }
}