package samurai.action.admin;

import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("shutdown")
@Source
@Client
public class Shutdown extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        return FixedMessage.createSimple("See ya later, loser.").setConsumer(message -> client.shutdown());
    }
}
