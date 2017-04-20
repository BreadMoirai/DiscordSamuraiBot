package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/14/2017
 */
@Key({"prev", "previous"})
public class Previous extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager guildAudioManager = managerOptional.get();
            guildAudioManager.scheduler.prevTrack();
            final AudioTrack currentTrack = guildAudioManager.player.getPlayingTrack();
            final AudioTrackInfo trackInfo = currentTrack.getInfo();
            final EmbedBuilder embedBuilder = new EmbedBuilder();
            final StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
            descriptionBuilder
                    .append(String.format("Playing [`%02d:%02d`/`%02d:%02d`]", currentTrack.getPosition() / (60 * 1000), currentTrack.getPosition() / 1000, trackInfo.length / (60 * 1000), (trackInfo.length / 1000) % 60))
                    .append("\n[").append(trackInfo.title).append("](").append(trackInfo.uri).append(")\n");
            return FixedMessage.build(embedBuilder.build());
        }
        return null;
    }
}
