package samurai.action.general;

import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@Key("info")
public class Info extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        return null;
    }
}
