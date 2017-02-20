package samurai.action.admin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import org.codehaus.groovy.control.CompilationFailedException;
import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.*;
import samurai.data.SamuraiStore;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/18/2017
 */
@Key("groovy")
@Client
@Admin
@Creator
@Osu
public class Groovy extends Action {

    private static final Binding binding;
    private static GroovyShell gs;

    static {
        binding = new Binding();
        binding.setVariable("creator", "DreadMoirai");
        binding.setVariable("bot", Bot.class);
        binding.setVariable("store", SamuraiStore.class);
        gs = new GroovyShell(binding);
    }

    public static void addBinding(String name, Object value) {
        binding.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage buildMessage() {

        client.getTextChannelById(String.valueOf(channelId)).deleteMessageById(String.valueOf(messageId)).queue();

        if (args.size() != 1) return FixedMessage.createSimple("Invalid Argument Length: " + args.size());
        binding.setVariable("chan", client.getTextChannelById(String.valueOf(channelId)));
        binding.setVariable("guild", client.getGuildById(String.valueOf(guildId)));
        try {
            Object result = gs.evaluate(args.get(0));
            if (result != null) {
                return FixedMessage.createSimple(String.format("```\n%s\n```", result.toString()));
            } else return FixedMessage.createSimple("Success.");
        } catch (CompilationFailedException | MissingPropertyException e) {
            return FixedMessage.createSimple("Failure.");
        }
    }

}
