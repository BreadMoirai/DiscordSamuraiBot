package samurai.command.music;

import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Admin
@Key("leave")
public class Leave extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.removeManager(context.getGuildId());
        managerOptional.ifPresent(audioManager -> {
            audioManager.player.destroy();
            audioManager.scheduler.clear();
        });
        context.getGuild().getAudioManager().closeAudioConnection();
        return null;
    }
}
