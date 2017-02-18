package samurai.action;

import samurai.annotations.Client;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.SamuraiMessage;

import javax.naming.Binding;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/17/2017
 */
@Key("groovy")
@Source
@Client
@Guild
public class Groovy extends Action {
    private static Binding binding = new Binding();


    @Override
    protected SamuraiMessage buildMessage() {
        return null;
    }
}
