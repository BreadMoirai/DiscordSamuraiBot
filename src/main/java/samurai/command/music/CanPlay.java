package samurai.command.music;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.utils.PermissionUtil;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4/12/2017
 */
@Key("canplay")
public class CanPlay extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Member selfMember = context.getGuild().getSelfMember();
        return FixedMessage.build(context.getGuild().getVoiceChannels().stream().map(voiceChannel -> (PermissionUtil.checkPermission(voiceChannel, selfMember, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK) ? "+" : "-") + voiceChannel.getName()).collect(Collectors.joining("\n", "```diff\n", "\n```")));


    }
}