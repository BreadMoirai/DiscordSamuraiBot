package samurai.command.music;

import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.dynamic.TrackLoader;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key({"q", "queue", "p", "play"})
public class Queue extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (!managerOptional.isPresent()) {
            return FixedMessage.build("Samurai has not joined a voice channel yet. Use `" + context.getKey() + "join [voice channel name]`.");
        }
        final GuildAudioManager audioManager = managerOptional.get();
        return new TrackLoader(audioManager, context.getContent(), context.getKey().startsWith("p"));
    }
}
