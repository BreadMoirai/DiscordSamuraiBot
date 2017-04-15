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
import samurai.messages.dynamic.TrackLoader;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key({"q", "queue", "p", "play", "playing", "playnext"})
public class Play extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (!managerOptional.isPresent()) {
            return FixedMessage.build("Samurai has not joined a voice channel yet. Use `" + context.getPrefix() + "join [voice channel name]`.");
        }
        final GuildAudioManager audioManager = managerOptional.get();
        if (context.hasContent()) {
            String content = context.getContent();
            if (content.startsWith("<") && content.endsWith(">")) {
                content = content.substring(1, content.length() - 1);
            }
            if (content.startsWith("yt ")) {
                content = "ytsearch:" + content.substring(3);
            } else if (content.startsWith("sc ")) {
                content = "scsearch:" + content.substring(3);
            }
            if (context.getKey().equalsIgnoreCase("playnext")) {
                return new TrackLoader(audioManager, content, false , true);
            }
            return new TrackLoader(audioManager, content, context.getKey().startsWith("p"), false);
        } else {
            final AudioTrack currentTrack = audioManager.scheduler.getCurrent();
            if (currentTrack == null)
                return FixedMessage.build("Nothing is playing right now. Try using `" + context.getPrefix() + "queue ytsearch:???`");
            AudioTrackInfo trackInfo = currentTrack.getInfo();
            EmbedBuilder eb = new EmbedBuilder();
            eb.appendDescription(String.format("Playing [`%02d:%02d`/`%02d:%02d`]", currentTrack.getPosition() / (60 * 1000), currentTrack.getPosition() / 1000, trackInfo.length / (60 * 1000), (trackInfo.length / 1000) % 60));
            eb.appendDescription("\n[" + trackInfo.title + "](" + trackInfo.uri + ")\n");
            eb.appendDescription("Up Next:");
            final AtomicInteger i = new AtomicInteger();
            final Collection<AudioTrack> tracks = audioManager.scheduler.getQueue();
            tracks.stream().limit(5).map(AudioTrack::getInfo).map(audioTrackInfo -> String.format("%n`%d.` [%s](%s) [%d:%02d]", i.incrementAndGet(), audioTrackInfo.title, audioTrackInfo.uri, audioTrackInfo.length / (60 * 1000), (audioTrackInfo.length / 1000) % 60)).forEachOrdered(eb::appendDescription);
            final int tSize = tracks.size();
            if (tSize > 5) {
                eb.appendDescription("\n... `" + (tSize - 5) + "` more tracks");
            }
            return FixedMessage.build(eb.build());
        }
    }
}

