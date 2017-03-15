package samurai.command.admin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.groovy.control.CompilationFailedException;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.data.SamuraiDatabase;
import samurai.data.SamuraiStore;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

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
    protected SamuraiMessage execute(CommandContext context) {
        final String content = context.getContent();
        if (content.length() <= 1) return null;
        binding.setVariable("messageId", Long.toString(context.getMessageId()));
        binding.setVariable("guildId", Long.toString(context.getGuildId()));
        binding.setVariable("channelId", Long.toString(context.getChannelId()));
        binding.setVariable("sg", context.getGuild());
        if (content.contains("binding")) {
            final Set set = binding.getVariables().entrySet();
            //noinspection unchecked
            return FixedMessage.build("|" + ((Set<Map.Entry<String, Object>>) set).stream().map(stringObjectEntry -> stringObjectEntry.getKey() + "=" + stringObjectEntry.getValue().getClass().getSimpleName() + "|").collect(Collectors.joining()));

        }

        try {
            Object result = gs.evaluate(content);
            if (result != null) {
                if (result instanceof byte[]) {
                    if (((byte[]) result).length == 0) {
                        return FixedMessage.build("Empty byte array");
                    }
                    return FixedMessage.build(Hex.encodeHexString((byte[]) result));
                }
                return FixedMessage.build(result.toString());
            } else return FixedMessage.build("Null");
        } catch (CompilationFailedException e) {
            return FixedMessage.build("Compilation Failure.");
        } catch (MissingPropertyException e) {
            return FixedMessage.build("Missing Property Failure.");
        } catch (MissingMethodException e) {
            return FixedMessage.build("Missing Method Failure.");
        } catch (NullPointerException e) {
            return FixedMessage.build("Null Pointer Failure");
        }
    }

}
