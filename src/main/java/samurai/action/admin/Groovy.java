package samurai.action.admin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import samurai.action.Action;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/18/2017
 */
@Key("groovy")
@Source
public class Groovy extends Action {

    private static final Binding binding;
    private static final GroovyShell gs;

    static {
        binding = new Binding();
        binding.setVariable("creator", "DreadMoirai");

        gs = new GroovyShell(binding);
    }

    @Override
    protected SamuraiMessage buildMessage() {
        return FixedMessage.createSimple(gs.evaluate(args.get(0)).toString());
    }
}
