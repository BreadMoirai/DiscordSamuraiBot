package samurai.action.admin;

import samurai.action.Action;
import samurai.annotations.Creator;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.MarkerMessage;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("shutdown")
@Source
@Creator
public class Shutdown extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        return new MarkerMessage();
    }
}
