package samurai.command.music;

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
 * @version 4/11/2017
 */
@Key({"vol", "volume"})
public class Volume extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            if (CommandContext.isInteger(context.getContent())) {
                final int newVol = Integer.parseInt(context.getContent());
                audioManager.player.setVolume(newVol);
            } else
                return FixedMessage.build("Volume: `" + audioManager.player.getVolume() + '`');
        }
        return null;
    }
}
