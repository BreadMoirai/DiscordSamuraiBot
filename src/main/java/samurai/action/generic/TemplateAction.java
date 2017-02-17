package samurai.action.generic;

import net.dv8tion.jda.core.MessageBuilder;
import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.DynamicMessage;
import samurai.message.dynamic.DynamicTemplate;
import samurai.message.fixed.FixedMessage;

/**
 * This class defines guidelines to creating a new action
 *
 * @author TonTL
 * @version 4.2
 * @since 2/16/2017
 */
@Key("template") //this denotes what command the user should type in ex. "!template"
public class TemplateAction extends Action {

    /**
     * build your message here. This is the only method that this class requires.
     *
     * @return a SamuraiMessage that will be passed to the controller to be sent to the originating channel
     * @see InviteAction A Simple Example
     */
    @Override
    protected SamuraiMessage buildMessage() {
        //Builds a simple fixed message that displays the parameters of the command.
        if (!args.contains("dynamic"))
            return buildFixedMessage();
        else
            return buildDynamicMessage();
    }

    /**
     * FixedMessages are a type of SamuraiMessage that is not intended to be persistent.
     * These will be sent once and forgotten
     *
     * @see FixedMessage
     */
    private FixedMessage buildFixedMessage() {
        FixedMessage samuraiMessage = new FixedMessage();
        //In order to build your message, you want to use a MessageBuilder
        MessageBuilder mb = new MessageBuilder();
        mb.append("This is a FixedMessage.\n");
        mb.append("```\n");
        //This Action object has several fields that you can access
        mb.append(String.format("%-10s|%s%n", "author", this.author.getUser().getName()))
                .append(String.format("%-10s| %s%n", "mentions", this.mentions))
                .append(String.format("%-10s| %s%n", "args", this.args))
                .append(String.format("%-10s| %s%n", "channelId", this.channelId))
                .append("```");
        //Now that you have a message built, simply add it to the FixedMessage
        samuraiMessage.setMessage(mb.build());

        // If you want to get a bit more complicated you can add a consumer to your samuraiMessage.
        // the lambda below adds a thumb up emoji to the message.
        // check using the command `$bin escape` on Discord on how to get the unicode of an emoji
        samuraiMessage.setConsumer(message -> message.addReaction("\uD83D\uDC4D").queue());
        return samuraiMessage;
    }

    /**
     * Builds a dynamic message that responds to user actions
     *
     * @return a dynamic message.
     * @see samurai.message.dynamic.DynamicTemplate
     */
    private DynamicMessage buildDynamicMessage() {
        //these are more complicated so you should create another class ? extends DynamicMessage in package message.dynamic.generic
        //Here is the example class
        //If your dynamic action requires any parameters, you should pass through the constructor.
        return new DynamicTemplate();
    }
}
