package samurai.action.admin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.codehaus.groovy.control.CompilationFailedException;
import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.*;
import samurai.data.SamuraiStore;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.nio.charset.StandardCharsets;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/18/2017
 */
@Key("groovy")
@Client
@Admin
@Creator
@Guild
public class Groovy extends Action {

    private static final Binding binding;
    private static final GroovyShell gs;

    static {
        binding = new Binding();
        binding.setVariable("creator", "DreadMoirai");
        binding.setVariable("bot", Bot.class);
        binding.setVariable("store", SamuraiStore.class);
        binding.setVariable("utf8", StandardCharsets.UTF_8);
        gs = new GroovyShell(binding);
    }

    public static void addBinding(String name, Object value) {
        binding.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage buildMessage() {
        if (args.size() != 1) return FixedMessage.build("Invalid Argument Length: " + args.size());
        binding.setVariable("chan", client.getTextChannelById(String.valueOf(channelId)));
        binding.setVariable("guild", client.getGuildById(String.valueOf(guildId)));
        binding.setVariable("sg", guild);
        try {
            Object result = gs.evaluate(args.get(0));
            if (result != null) {
                return FixedMessage.build(result.toString());
            } else return FixedMessage.build("Success.");
        } catch (CompilationFailedException e) {
            return FixedMessage.build("Compilation Failure.");
        } catch (MissingPropertyException e) {
            return FixedMessage.build("Missing Property Failure.");
        } catch (MissingMethodException e) {
            return FixedMessage.build("Missing Method Failure.");
        }
    }

}
