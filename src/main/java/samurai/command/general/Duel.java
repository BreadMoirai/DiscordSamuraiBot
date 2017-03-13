package samurai.command.general;

import samurai.command.Command;
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
    public SamuraiMessage buildMessage() {
        if (mentions.size() != 1)
            return new ConnectFour(author.getUser());
        else
            return new ConnectFour(author.getUser(), mentions.get(0).getUser());
    }
}
