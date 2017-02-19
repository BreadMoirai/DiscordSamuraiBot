package samurai.action.admin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Admin;
import samurai.annotations.Client;
import samurai.annotations.Key;
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
public class Groovy extends Action {

    private static final Binding binding;
    private static GroovyShell gs;

    static {
        binding = new Binding();
        binding.setVariable("creator", "DreadMoirai");

        gs = new GroovyShell(binding);
    }

    public static void addBinding(String name, Object value) {
        binding.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage buildMessage() {
        client.getTextChannelById(String.valueOf(channelId)).deleteMessageById(String.valueOf(messageId)).queue();
        binding.setVariable("chan", client.getTextChannelById(String.valueOf(channelId)));
        binding.setVariable("guild", client.getGuildById(String.valueOf(guildId)));
        try {
            Object result = gs.evaluate(args.get(0));
            if (result != null) {
                return FixedMessage.createSimple(result.toString());
            }
        } catch (MissingPropertyException e) {
            Bot.log(e.getMessageWithoutLocationText());
        }
        return null;
    }

}
