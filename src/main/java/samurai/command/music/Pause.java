package samurai.command.music;

import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/12/2017
 */
@Key({"pause", "unpause"})
public class Pause extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        managerOptional.ifPresent(audioManager -> audioManager.player.setPaused(!context.getKey().toLowerCase().startsWith("un")));
        return null;
    }
}
