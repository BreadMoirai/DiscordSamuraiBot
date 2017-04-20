package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.annotations.MessageScope;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.test.UniqueTestMessage;

/**
 * @author TonTL
 * @version 4/19/2017
 */
@Key("unique")
public class UniqueTest extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        switch (context.getContent().toLowerCase()) {
            case "author":
                return new UniqueTestMessage(MessageScope.Author);
            case "channel":
                return new UniqueTestMessage(MessageScope.Channel);
            case "guild":
                return new UniqueTestMessage(MessageScope.Guild);
            default:
                return null;
        }
    }
}
