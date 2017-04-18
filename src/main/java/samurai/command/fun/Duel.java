package samurai.command.fun;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.duel.ConnectFour;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("duel")
public class Duel extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (context.getMentionedMembers().size() != 1)
            return new ConnectFour(context.getAuthor().getUser());
        else
            return new ConnectFour(context.getAuthor().getUser(), context.getMentionedMembers().get(0).getUser());
    }
}
