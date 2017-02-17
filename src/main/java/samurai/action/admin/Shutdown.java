package samurai.action.admin;

import net.dv8tion.jda.core.MessageBuilder;
import samurai.action.Action;
import samurai.annotations.Admin;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("shutdown")
@Admin
@Client
public class Shutdown extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        return new FixedMessage().setMessage(new MessageBuilder().append("See ya later, loser.").build()).setConsumer(message -> client.shutdown());
    }
}
