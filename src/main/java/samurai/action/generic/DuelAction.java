package samurai.action.generic;

import samurai.action.Action;
import samurai.action.Key;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.duel.ConnectFour;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("duel")
public class DuelAction extends Action {

    @Override
    public SamuraiMessage buildMessage() {
        if (mentions.size() != 1)
            return new ConnectFour(author.getUser());
        else
            return new ConnectFour(author.getUser(), mentions.get(0));
    }
}
