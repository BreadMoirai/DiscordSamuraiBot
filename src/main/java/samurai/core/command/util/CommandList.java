package samurai.core.command.util;

import samurai.core.command.Command;
import samurai.core.command.CommandFactory;
import samurai.core.command.annotations.Key;
import samurai.core.entities.FixedMessage;
import samurai.core.entities.SamuraiMessage;

/**
 * @author TonTL
 * @version 3/8/2017
 */
@Key({"cmdall", "cmdlist"})
public class CommandList extends Command {
    @Override
    protected SamuraiMessage buildMessage() {
        StringBuilder sb = new StringBuilder().append("```");
        CommandFactory.keySet().forEach(s -> sb.append(s).append(' '));
        sb.append("```");
        return FixedMessage.build(sb.toString());
    }
}
