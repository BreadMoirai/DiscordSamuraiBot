package samurai.command.music;

import com.google.api.services.youtube.model.Playlist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.audio.TrackScheduler;
import samurai.audio.YoutubeAPI;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.music.TrackLoader;

import java.util.List;
import java.util.Optional;

/**
 * @author TonTL
 * @version 4/19/2017
 */
@Key("related")
public class Related extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            final AudioTrack playingTrack = audioManager.player.getPlayingTrack();
            if (playingTrack != null) {
                final String uri = playingTrack.getInfo().uri;
                if (uri.toLowerCase().contains("youtube")) {
                    final List<String> related = YoutubeAPI.getRelated(uri.substring(uri.lastIndexOf('=') + 1), 20L);
                    return new TrackLoader(audioManager, related, String.format("Tracks related to [%s](%s)", playingTrack.getInfo().title, uri));
                }
            }
        }
        return null;
    }
}
