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
package com.github.breadmoirai.samurai.modules.music;

import com.github.breadmoirai.samurai7.core.CommandEvent;
import com.github.breadmoirai.samurai7.core.IModule;
import com.github.breadmoirai.samurai7.core.SamuraiClient;
import com.github.breadmoirai.samurai7.core.impl.CommandEngineBuilder;
import com.github.breadmoirai.samurai7.core.response.Response;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai7.database.Database;
import com.github.breadmoirai.samurai.util.PermissionFailureResponse;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MusicModule implements IModule {

    private final int defaultVolume;

    private final AudioPlayerManager playerManager;
    private final TLongObjectMap<GuildMusicManager> audioManagers;

    public MusicModule(int defaultVolume) {
        this.defaultVolume = defaultVolume;
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        audioManagers = new TLongObjectHashMap<>();
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    @Override
    public void init(CommandEngineBuilder commands, SamuraiClient samuraiClient) {
        commands.registerCommand("com.github.breadmoirai.samuraiBot.modules.music.commands");

        Database db = Database.get();
        if (!db.tableExists("GuildVolume"))
            db.useHandle(handle -> {
                handle.execute("CREATE TABLE GuildVolume (" +
                        "id BIGINT PRIMARY KEY, " +
                        "vol SMALLINT" +
                        ")");
            });
        if (db.tableExists("DesignatedMusicChannel"))
            db.useHandle(handle -> {
                handle.execute("CREATE TABLE MusicChannel (" +
                        "gid BIGINT, " +
                        "cid BIGINT, " +
                        "CONSTRAINT MusicChannel_PK PRIMARY KEY (gid, cid)" +
                        ")");
            });
    }

    public void updateVolume(long guildId, int vol) {
        Database.get().useHandle(handle -> {
            if (handle.createUpdate("UPDATE GuildVolume " +
                    "SET vol = :? " +
                    "WHERE id = :?")
                    .bind(0, vol)
                    .bind(1, guildId)
                    .execute() == 1) {
                retrieveManager(guildId).ifPresent(guildMusicManager -> guildMusicManager.setVolume(vol));
            }
        });

    }

    public int getVolume(long guildId) {
        return Database.get().withHandle(handle -> handle
                .createQuery("SELECT vol FROM GuildVolume " +
                        "WHERE id = :?")
                .bind(0, guildId)
                .mapTo(Integer.class)
                .findFirst()
                .orElseGet(() -> {
                    handle.createUpdate("INSERT INTO GuildVolume VALUES (:?, :?)")
                            .bind(0, guildId)
                            .bind(1, defaultVolume)
                            .execute();
                    return defaultVolume;
                }));
    }

    public void openConnection(VoiceChannel channel) {
        final long idLong = channel.getGuild().getIdLong();
        if (!audioManagers.containsKey(idLong))
            audioManagers.putIfAbsent(idLong, new GuildMusicManager(this, channel.getGuild().getAudioManager(), getVolume(channel.getGuild().getIdLong())));
        audioManagers.get(idLong).openConnection(channel);
    }

    public void loadItem(Object orderingKey, String request, AudioLoadResultHandler resultHandler) {
        if (orderingKey instanceof GuildMusicManager)
            orderingKey = ((GuildMusicManager) orderingKey).getScheduler();
        playerManager.loadItemOrdered(orderingKey, request, resultHandler);
    }

    public Optional<GuildMusicManager> retrieveManager(long guildId) {
        return Optional.ofNullable(audioManagers.get(guildId));
    }

    AudioPlayer createPlayer() {
        return playerManager.createPlayer();
    }

    public static String trackInfoDisplay(AudioTrack track, boolean displayName, boolean hyperLink) {
        if (track == null) return "Nothing";
        AudioTrackInfo trackInfo = track.getInfo();
        String trackLengthDisp;
        if (trackInfo.length == Long.MAX_VALUE) {
            trackLengthDisp = "\u221e";
        } else {
            trackLengthDisp = String.format("%d:%02d", trackInfo.length / (60 * 1000), trackInfo.length / 1000 % 60);
        }
        if (displayName && track.getUserData() != null) {
            if (hyperLink) {
                return String.format("[%s](%s) [%s] _%s_", trackInfo.title, trackInfo.uri, trackLengthDisp, track.getUserData(String.class));
            } else {
                return String.format("%s [%s] _%s_", trackInfo.title, trackLengthDisp, track.getUserData(String.class));
            }
        } else if (hyperLink) {
            return String.format("[%s](%s) [%s]", trackInfo.title, trackInfo.uri, trackLengthDisp);
        } else {
            return String.format("%s [%s]", trackInfo.title, trackLengthDisp);
        }
    }

    @Nullable
    public Response checkConnection(CommandEvent event, boolean openConnection) {
        final Member selfMember = event.getSelfMember();
        if (!selfMember.hasPermission(event.getChannel(), Permission.MESSAGE_ADD_REACTION))
            return new PermissionFailureResponse(selfMember, event.getChannel(), Permission.MESSAGE_ADD_REACTION);
        final VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();
        if (memberChannel == null)
            return Responses.of("You must be in a voice channel to queue audio tracks.");
        final Optional<GuildMusicManager> musicManagerOptional = this.retrieveManager(event.getGuildId());
        if (musicManagerOptional.isPresent()) {
            GuildMusicManager musicManager = musicManagerOptional.get();
            if (musicManager.isConnected()) {
                if (!musicManager.getConnectedChannel().equals(memberChannel))
                    return Responses.of("You must be in the same voice channel as me to queue audio tracks.");
            }
        } else {
            if (!selfMember.hasPermission(memberChannel, Permission.VOICE_SPEAK, Permission.VOICE_CONNECT)) {
                return new PermissionFailureResponse(selfMember, memberChannel, Permission.VOICE_SPEAK, Permission.VOICE_CONNECT);
            }
            if (openConnection)
                this.openConnection(memberChannel);
        }
        return null;
    }
}

