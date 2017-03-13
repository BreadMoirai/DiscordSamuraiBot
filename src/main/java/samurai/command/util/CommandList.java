package samurai.command.util;

import samurai.command.Command;
import samurai.command.CommandFactory;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

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
