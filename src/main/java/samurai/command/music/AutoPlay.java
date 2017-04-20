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
 * @version 4/15/2017
 */
@Key("autoplay")
public class AutoPlay extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            if (context.hasContent()) {
                final String content = context.getContent();
                if (content.equalsIgnoreCase("t") || content.equalsIgnoreCase("true")) {
                    audioManager.scheduler.setAutoPlay(true);
                    return FixedMessage.build("AutoPlay set to `true`");
                } else if (content.equalsIgnoreCase("f") || content.equalsIgnoreCase("false")) {
                    audioManager.scheduler.setAutoPlay(false);
                    return FixedMessage.build("AutoPlay set to `false`");
                }
            } else {
                return FixedMessage.build("AutoPlay is currently `" + (audioManager.scheduler.isAutoPlay() ? "enabled`" : "disabled`"));
            }
        }
        return null;
    }
}
