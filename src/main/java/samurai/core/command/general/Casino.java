package samurai.core.command.general;

import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.entities.base.SamuraiMessage;
import samurai.core.entities.dynamic.black_jack.BlackJack;

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
