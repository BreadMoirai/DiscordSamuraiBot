package samurai.command.osu;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.database.DatabaseSingleton;
import samurai.entities.model.SGuild;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.enums.GameMode;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/17/2017
 */
@Key("setchannel")
public class SetChannel extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getArgs().size() != 1) {
            return null;
        }
        TextChannel targetChannel;
        if (context.getMentionedChannels().size() == 1) {
            targetChannel = context.getMentionedChannels().get(0);
        } else {
            targetChannel = context.getChannel();
        }
        final GameMode gameMode = GameMode.find(context.getStrippedContent());
        final Optional<SGuild> guildOptional = DatabaseSingleton.getDatabase().getGuild(context.getGuildId());
        if (guildOptional.isPresent()) {
            final SGuild sGuild = guildOptional.get();
            if (gameMode != null) {
                sGuild.getManager().addChannelFilter(targetChannel.getIdLong(), gameMode);
                return FixedMessage.build("Tracking notifications will be sent to <#" + targetChannel.getId() + "> for `" + gameMode.toString() + "`");
            }
            else {
                sGuild.getManager().removeChannelFilter(targetChannel.getIdLong());
                return FixedMessage.build("<#" + targetChannel.getId() + "> will no longer receive tracking notifications");
            }
        }
        return null;
    }
}
