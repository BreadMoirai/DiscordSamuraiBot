package samurai.command.music;

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
@Key({"v", "volume"})
public class Volume extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        int temp;
        try {
            temp = Integer.parseInt(context.getContent());
        } catch (NumberFormatException e) {
            temp = -1;
        }
        final int newVol = temp;
        if (newVol > 200 || newVol < 0)
            return FixedMessage.build("Not a valid Integer 1-200");
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        managerOptional.ifPresent(audioManager -> audioManager.player.setVolume(newVol));
        return null;
    }
}
