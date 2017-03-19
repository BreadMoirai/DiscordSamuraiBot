package samurai.command.general;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.util.wrappers.SamuraiWrapper;

/**
 * @author TonTL
 * @version 3/14/2017
 */
@Key("menu")
public class Menu extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return FixedMessage.build("").setConsumer(message -> SamuraiWrapper.wrap(message).addReaction(context.getArgs()));
    }
}
