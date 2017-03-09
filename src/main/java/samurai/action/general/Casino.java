package samurai.action.general;

import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.black_jack.BlackJack;

/**
 * @author TonTL
 * @version 3/8/2017
 */
@Key({"casino", "blackjack", "bj"})
public class Casino extends Action {
    @Override
    protected SamuraiMessage buildMessage() {
        return new BlackJack();
    }
}
