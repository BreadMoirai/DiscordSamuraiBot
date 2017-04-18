package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.music.TrackLoader;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key({"queue", "play", "playing", "playnow", "queuef"})
public class Play extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (!managerOptional.isPresent()) {
            return FixedMessage.build("Samurai has not joined a voice channel yet. Use `" + context.getPrefix() + "join [voice channel name]`.");
        }
        final GuildAudioManager audioManager = managerOptional.get();
        if (context.hasContent()) {
            boolean lucky = context.getKey().equalsIgnoreCase("queuef");
            String content = context.getContent();
            if (content.startsWith("<") && content.endsWith(">")) {
                content = content.substring(1, content.length() - 1);
            }
            if (content.startsWith("yt ")) {
                content = "ytsearch:" + content.substring(3);
            } else if (content.startsWith("sc ")) {
                content = "scsearch:" + content.substring(3);
            }
            if (context.getKey().equalsIgnoreCase("playnow")) {
                return new TrackLoader(audioManager, content, true, true, lucky);
            }
            return new TrackLoader(audioManager, content, false, context.getKey().startsWith("p"), lucky);
        } else {
            final AudioTrack currentTrack = audioManager.scheduler.getCurrent();
            if (currentTrack == null)
                return FixedMessage.build("Nothing is playing right now. Look at <#302662195270123520>");
            AudioTrackInfo trackInfo = currentTrack.getInfo();
            EmbedBuilder eb = new EmbedBuilder();
            String trackLengthDisp;
            if (trackInfo.length == Long.MAX_VALUE) {
                trackLengthDisp = "∞";
            } else {
                trackLengthDisp = String.format("%d:%02d", trackInfo.length / (60 * 1000), trackInfo.length / 1000 % 60);
            }
            eb.appendDescription(String.format("Playing [`%02d:%02d`/`%s`]", currentTrack.getPosition() / (60 * 1000), (currentTrack.getPosition() / 1000) % 60, trackLengthDisp));
            eb.appendDescription("\n[" + trackInfo.title + "](" + trackInfo.uri + ")\n");
            final Collection<AudioTrack> tracks = audioManager.scheduler.getQueue();
            if (!tracks.isEmpty()) {
                eb.appendDescription("Up Next:");
                final AtomicInteger i = new AtomicInteger();
                tracks.stream().limit(8).map(AudioTrack::getInfo).map(audioTrackInfo -> String.format("%n`%d.` %s", i.incrementAndGet(), trackInfoDisplay(audioTrackInfo))).forEachOrdered(eb::appendDescription);
                final int tSize = tracks.size();
                if (tSize > 8) {
                    eb.appendDescription("\n... `" + (tSize - 8) + "` more tracks");
                }
            }
            return FixedMessage.build(eb.build());
        }
    }

    public static String trackInfoDisplay(AudioTrackInfo trackInfo) {
        String trackLengthDisp;
        if (trackInfo.length == Long.MAX_VALUE) {
            trackLengthDisp = "∞";
        } else {
            trackLengthDisp = String.format("%d:%02d", trackInfo.length / (60 * 1000), trackInfo.length / 1000 % 60);
        }
        return String.format("[%s](%s) [`%s`]", trackInfo.title, trackInfo.uri, trackLengthDisp);
    }
}

