package samurai.command.general;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.base.SamuraiMessage;
import samurai.entities.dynamic.duel.ConnectFour;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("duel")
public class Duel extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (context.getMentions().size() != 1)
            return new ConnectFour(context.getAuthor().getUser());
        else
            return new ConnectFour(context.getAuthor().getUser(), context.getMentions().get(0).getUser());
    }
}
