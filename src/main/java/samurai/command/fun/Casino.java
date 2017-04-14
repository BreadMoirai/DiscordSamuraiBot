package samurai.command.fun;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.dynamic.black_jack.Poker;

/**
 * @author TonTL
 * @version 3/8/2017
 */
@Key("casino")
public class Casino extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return new Poker();
    }
}
