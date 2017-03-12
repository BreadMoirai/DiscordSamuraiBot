package samurai.core.command.general;

import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.entities.base.SamuraiMessage;
import samurai.core.entities.dynamic.duel.ConnectFour;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("duel")
public class Duel extends Command {

    @Override
    public SamuraiMessage buildMessage() {
        if (mentions.size() != 1)
            return new ConnectFour(author.getUser());
        else
            return new ConnectFour(author.getUser(), mentions.get(0).getUser());
    }
}
