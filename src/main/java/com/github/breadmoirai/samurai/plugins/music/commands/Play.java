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
package com.github.breadmoirai.samurai.plugins.music.commands;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
import com.github.breadmoirai.samurai.plugins.music.TrackLoader;
import com.github.breadmoirai.samurai.util.MiscUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Play {
    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    public static String trackInfoDisplay(AudioTrack track) {
        return trackInfoDisplay(track, true);
    }

    public static String trackInfoDisplay(AudioTrack track, boolean displayName) {
        if (track == null) return "Nothing";
        AudioTrackInfo trackInfo = track.getInfo();
        String trackLengthDisp;
        if (trackInfo.length == Long.MAX_VALUE) {
            trackLengthDisp = "\u221e";
        } else {
            trackLengthDisp = String.format("%d:%02d", trackInfo.length / (60 * 1000), trackInfo.length / 1000 % 60);
        }
        if (displayName && track.getUserData() != null)
            return String.format("[%s](%s) [%s] _%s_", trackInfo.title, trackInfo.uri, trackLengthDisp, track.getUserData(String.class));
        else return String.format("[%s](%s) [%s]", trackInfo.title, trackInfo.uri, trackLengthDisp);
    }

    @MainCommand({"queue", "play", "nowplaying"})
    public TrackLoader onCommand(CommandEvent event, MusicPlugin plugin) {
        if (event.requirePermission(PERMISSIONS)) {
            return null;
        }
        Optional<GuildAudioManager> managerOptional = plugin.retrieveManager(event.getGuildId());
        if (!managerOptional.isPresent()) {
            final VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            if (channel == null) {
                event.reply("Samurai has not joined a voice channel yet. Use `" + event.getPrefix() + "join [voice channel name]`.");
                return null;
            } else if (plugin.openConnection(channel)) {
                managerOptional = plugin.retrieveManager(event.getGuildId());
            } else {
                event.reply("Could not open voice connection");
                return null;
            }
            if (!managerOptional.isPresent()) {
                event.reply("Could not retrieve voice connection");
                return null;
            }
        }
        final GuildAudioManager audioManager = managerOptional.get();
        if (event.hasContent()) {
            boolean lucky = event.getKey().equalsIgnoreCase("play");
            final String asUrl = MiscUtil.getAsUrl(event.getContent());
            if (asUrl != null) {
                return new TrackLoader(plugin, audioManager, true, asUrl);
            }
            String content = event.getContent();
            if (content.startsWith("yt ")) {
                content = "ytsearch:" + content.substring(3);
            } else if (content.startsWith("sc ")) {
                content = "scsearch:" + content.substring(3);
            } else {
                content = "ytsearch:" + content;
            }
            return new TrackLoader(plugin, audioManager, lucky, content);
        } else {
            final MessageEmbed embed = nowPlaying(audioManager);
            if (embed == null) {
                event.reply("Nothing is playing right now.");
            }
            event.reply().setEmbed(embed);
        }
        return null;
    }

    private MessageEmbed nowPlaying(GuildAudioManager audioManager) {
        final AudioTrack currentTrack = audioManager.scheduler.getCurrent();
        if (currentTrack == null)
            return null;
        EmbedBuilder eb = new EmbedBuilder();
        eb.appendDescription(String.format("Playing [`%02d:%02d`/`%02d:%02d`]", currentTrack.getPosition() / (60 * 1000), (currentTrack.getPosition() / 1000) % 60, currentTrack.getDuration() / (60 * 1000), (currentTrack.getDuration() / 1000) % 60));
        eb.appendDescription("\n[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")");
        final String userData = currentTrack.getUserData(String.class);
        if (userData != null)
            eb.appendDescription(" `").appendDescription(userData).appendDescription("`\n");
        else eb.appendDescription("\n");
        final Collection<AudioTrack> tracks = audioManager.scheduler.getQueue();
        if (!tracks.isEmpty()) {
            eb.appendDescription("Up Next:");
            final AtomicInteger i = new AtomicInteger();
            tracks.stream().limit(10).map(audioTrackInfo -> String.format("%n`%d.` %s", i.incrementAndGet(), trackInfoDisplay(audioTrackInfo, true))).forEachOrdered(eb::appendDescription);
            final int tSize = tracks.size();
            if (tSize > 10) {
                eb.appendDescription("\n... `" + (tSize - 10) + "` more tracks");
            }
        }
        return eb.build();
    }
}

