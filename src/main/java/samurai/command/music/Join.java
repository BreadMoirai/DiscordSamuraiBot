package samurai.command.music;

import net.dv8tion.jda.core.entities.VoiceChannel;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.List;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key("join")
public class Join extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<VoiceChannel> voiceChannelsByName = context.getDiscordGuild().getVoiceChannelsByName(context.getContent(), true);
        if (voiceChannelsByName.isEmpty()) {
            return FixedMessage.build("The specificed Voice Channel was not found.");
        }
        SamuraiAudioManager.openConnection(voiceChannelsByName.get(0));
        return null;
    }
}
