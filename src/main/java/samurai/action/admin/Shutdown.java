package samurai.action.admin;

import samurai.action.Action;
import samurai.annotations.Controller;
import samurai.annotations.Creator;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("shutdown")
@Source
@Controller
@Creator
public class Shutdown extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        controller.shutdown();
        return null;
    }
}
