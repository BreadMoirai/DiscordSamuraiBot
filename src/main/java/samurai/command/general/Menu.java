package samurai.command.general;

import net.dv8tion.jda.core.requests.RestAction;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 3/14/2017
 */
@Key("menu")
public class Menu extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return FixedMessage.build("Placeholder"/*"Use `" + context.getPrefix() + "setContent` to change what I am saying"*/).setConsumer(message -> context.getArgs().stream().map(message::addReaction).forEach(RestAction::queue));
    }
}
