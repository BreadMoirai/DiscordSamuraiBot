package samurai.command.music;

import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4/19/2017
 */
@Key("shuffle")
public class Shuffle extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return SamuraiAudioManager.retrieveManager(context.getGuildId()).<SamuraiMessage>map(guildAudioManager -> FixedMessage.build("`" + guildAudioManager.scheduler.shuffleQueue() + "` tracks shuffled")).orElse(null);
    }
}
