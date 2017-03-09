package samurai.action.util;

import samurai.action.Action;
import samurai.action.ActionFactory;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 3/8/2017
 */
@Key({"cmdall", "cmdlist"})
public class CommandList extends Action {
    @Override
    protected SamuraiMessage buildMessage() {
        StringBuilder sb = new StringBuilder().append("```");
        ActionFactory.keySet().forEach(s -> sb.append(s).append(' '));
        sb.append("```");
        return FixedMessage.build(sb.toString());
    }
}
