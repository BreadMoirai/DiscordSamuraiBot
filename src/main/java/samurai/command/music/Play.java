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
package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.impl.music.TrackLoader;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key({"queue", "play", "playing", "playnow"})
public class Play extends Command {
    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (!managerOptional.isPresent()) {
            final VoiceChannel channel = context.getAuthor().getVoiceState().getChannel();
            if (channel == null)
                return FixedMessage.build("Samurai has not joined a voice channel yet. Use `" + context.getPrefix() + "join [voice channel name]`.");
            else if (SamuraiAudioManager.openConnection(channel)) {
                managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
            } else {
                return FixedMessage.build("Could not open voice connection");
            }
            if (!managerOptional.isPresent()) {
                return FixedMessage.build("Could not retrieve voice connection");
            }
        }
        final GuildAudioManager audioManager = managerOptional.get();
        if (context.hasContent()) {
            boolean lucky = context.getKey().equalsIgnoreCase("play");
            final String asUrl = context.getAsUrl();
            if (asUrl != null) {
                return new TrackLoader(audioManager, false, lucky, asUrl);
            }
            String content = context.getContent();
            if (content.startsWith("yt ")) {
                content = "ytsearch:" + content.substring(3);
            } else if (content.startsWith("sc ")) {
                content = "scsearch:" + content.substring(3);
            } else {
                content = "ytsearch: " + content;
            }
            if (context.getKey().equalsIgnoreCase("playnow")) {
                return new TrackLoader(audioManager, true, lucky, content);
            }
            return new TrackLoader(audioManager, false, lucky, content);
        } else {
            final MessageEmbed embed = nowPlaying(audioManager);
            if (embed == null) {
                return FixedMessage.build("Nothing is playing right now.");
            }
            return FixedMessage.build(embed);
        }

    }

    private MessageEmbed nowPlaying(GuildAudioManager audioManager) {
        final AudioTrack currentTrack = audioManager.scheduler.getCurrent();
        if (currentTrack == null)
            return null;
        EmbedBuilder eb = new EmbedBuilder();
        eb.appendDescription(String.format("Playing [`%02d:%02d`/`%02d:%02d`]", currentTrack.getPosition() / (60 * 1000), (currentTrack.getPosition() / 1000) % 60, currentTrack.getDuration() / (60 * 1000), (currentTrack.getDuration() / 1000) % 60));
        eb.appendDescription("\n[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")");
        eb.appendDescription(" `").appendDescription(currentTrack.getUserData(String.class)).appendDescription("`\n");
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
}

