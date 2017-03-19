package samurai.command.restricted;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.codec.binary.Hex;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.Commands;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.data.SamuraiDatabase;
import samurai.data.SamuraiStore;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

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
        binding.setVariable("cf", CommandFactory.class);
        binding.setVariable("CD", Commands.class);
        binding.setVariable("CDP", Commands.CommandCP.class);
        gs = new GroovyShell(binding);

    }

    public static void addBinding(String name, Object value) {
        binding.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final String content = context.getContent().replaceAll("`", "");
        if (content.length() <= 1) return null;
        binding.setVariable("messageId", Long.toString(context.getMessageId()));
        binding.setVariable("guildId", Long.toString(context.getGuildId()));
        binding.setVariable("channelId", Long.toString(context.getChannelId()));
        binding.setVariable("sg", context.getGuild());
        if (content.contains("binding")) {
            final Set set = binding.getVariables().entrySet();
            //noinspection unchecked
            return FixedMessage.build(((Set<Map.Entry<String, Object>>) set).stream().map(stringObjectEntry -> stringObjectEntry.getKey() + "=" + stringObjectEntry.getValue().getClass().getSimpleName()).collect(Collectors.joining("\n")));

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
        } catch (Exception e) {
            return FixedMessage.build(e.getMessage());
        }
    }

}
