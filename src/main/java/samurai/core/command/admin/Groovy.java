package samurai.core.command.admin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.groovy.control.CompilationFailedException;
import samurai.core.Bot;
import samurai.core.command.Command;
import samurai.core.command.annotations.Admin;
import samurai.core.command.annotations.Creator;
import samurai.core.command.annotations.Key;
import samurai.core.entities.base.FixedMessage;
import samurai.core.entities.base.SamuraiMessage;
import samurai.data.SamuraiDatabase;
import samurai.data.SamuraiStore;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author TonTL
 * @version 5.0
 * @since 2/18/2017
 */
@Key({"groovy", "g"})
@Admin
@Creator
public class Groovy extends Command {

    private static final Binding binding;
    private static final GroovyShell gs;

    static {
        binding = new Binding();
        binding.setVariable("creator", "DreadMoirai");
        binding.setVariable("bot", Bot.class);
        binding.setVariable("store", SamuraiStore.class);
        binding.setVariable("db", SamuraiDatabase.class);

        gs = new GroovyShell(binding);

    }

    public static void addBinding(String name, Object value) {
        binding.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage buildMessage() {
        if (args.size() != 1) return FixedMessage.build("Invalid Argument Length: " + args.size());
        binding.setVariable("messageId", Long.toString(messageId));
        binding.setVariable("guildId", Long.toString(guildId));
        binding.setVariable("channelId", Long.toString(channelId));
        binding.setVariable("sg", guild);
        if (args.size() == 1 && args.get(0).equalsIgnoreCase("bindings")) {
            final Set set = binding.getVariables().entrySet();
            //noinspection unchecked
            return FixedMessage.build("|" + ((Set<Map.Entry<String, Object>>) set).stream().map(stringObjectEntry -> stringObjectEntry.getKey() + "=" + stringObjectEntry.getValue().getClass().getSimpleName() + "|").collect(Collectors.joining()));

        }

        try {
            Object result = gs.evaluate(args.get(0));
            if (result != null) {
                if (result instanceof byte[]) {
                    if (((byte[]) result).length == 0) {
                        return FixedMessage.build("Empty Array");
                    }
                    return FixedMessage.build(Hex.encodeHexString((byte[]) result));
                }
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
