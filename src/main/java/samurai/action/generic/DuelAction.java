package samurai.action.generic;

import samurai.action.Action;
import samurai.message.SamuraiMessage;
import samurai.message.duel.ConnectFour;

/**
 * @author TonTL
 * @since 4.0
 */
public class DuelAction extends Action {

    @Override
    public SamuraiMessage buildMessage() {
        if (mentions.size() != 1)
            return new ConnectFour(author.getUser());
        else
            return new ConnectFour(author.getUser(), mentions.get(0));
    }
}
