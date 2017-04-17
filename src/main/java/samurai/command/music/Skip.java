package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key("skip")
public class Skip extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            AudioTrack current = audioManager.player.getPlayingTrack();
            if (context.hasContent()) {
                if (context.getContent().equalsIgnoreCase("all")) {
                    audioManager.scheduler.clear();
                } else {
                    audioManager.scheduler.skip(context.getIntArgs().boxed());
                }
            } else {
                audioManager.scheduler.nextTrack();
            }
            return FixedMessage.build(new EmbedBuilder()
                    .appendDescription("Skipped:\n")
                    .appendDescription("[")
                    .appendDescription(current.getInfo().title)
                    .appendDescription("](")
                    .appendDescription(current.getInfo().uri)
                    .appendDescription(")").build());
        }
        return null;
    }
}
