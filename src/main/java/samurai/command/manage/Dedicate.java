package samurai.command.manage;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
@Key({"setchannel", "dedicate"})
@Admin
public class Dedicate extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getMentionedChannels().size() == 1) {
            context.getGuild().setDedicatedChannel(context.getMentionedChannels().get(0).getId());
            return FixedMessage.build("All tracking notifications will be sent to `" + context.getMentionedChannels().get(0).getName() + '`');
        } else return null;
    }
}
