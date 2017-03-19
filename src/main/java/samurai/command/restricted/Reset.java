package samurai.command.restricted;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
@Creator
public class Reset extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return null;
    }
}
