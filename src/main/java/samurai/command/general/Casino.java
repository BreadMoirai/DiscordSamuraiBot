package samurai.command.general;

import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.entities.base.SamuraiMessage;
import samurai.entities.dynamic.black_jack.BlackJack;

/**
 * @author TonTL
 * @version 3/8/2017
 */
@Key({"casino", "blackjack", "bj"})
public class Casino extends Command {
    @Override
    protected SamuraiMessage buildMessage() {
        return new BlackJack();
    }
}
