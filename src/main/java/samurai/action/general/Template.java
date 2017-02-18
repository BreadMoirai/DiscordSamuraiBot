package samurai.action.general;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import samurai.action.Action;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.DynamicMessage;
import samurai.message.dynamic.DynamicTemplate;
import samurai.message.fixed.FixedMessage;

/**
 * This class defines guidelines to creating a new action
 *
 * @author TonTL
 * @version 4.2
 * @see Action
 * @see DynamicTemplate
 * @since 2/16/2017
 */
@Key("template") //this denotes what command the user should type in ex. "!template"
//keys should be less than 10 characters
@Source // can only be used in DreadMoirai's Samurais;
public class Template extends Action {

    /**
     * build your message here. This is the only method that this class requires.
     *
     * @return a SamuraiMessage that will be passed to the controller to be sent to the originating channel
     * @see Invite A Simple Example
     * @see DynamicTemplate an example for dynamic messages
     */
    @Override
    protected SamuraiMessage buildMessage() {
        if (args.contains("simple"))
            //returns a simple text message
            //use FixedMessage.createSimple("message"); if you just want to acknowledge the user.
            return FixedMessage.createSimple("This is an example message");
        else if (args.contains("dynamic"))
            //returns a dynamic message
            //see Dynamic Template
            return buildDynamicMessage();
        else
            //builds a fixed message with all the params
            //see below
            return buildFixedMessage();

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
        mb.append(String.format("%-10s| %s%n", "author", this.author.getUser().getName()))
                .append(String.format("%-10s| %s%n", "channelId", this.channelId));
        //get the mentions
        if (!mentions.isEmpty()) {
            int i = 0;
            mb.append(String.format("%-10s| %s%n", "mentions", "----"));
            for (User u : mentions) mb.append(String.format("%8s. | %s%n", i++ + 1, u.getName()));
        }
        //get the args
        if (!args.isEmpty()) {
            int i = 0;
            mb.append(String.format("%-10s| %s%n", "arguments", "----"));
            for (String arg : args) mb.append(String.format("%8s. | %s%n", i++ + 1, arg.replace("\n", "[\\n]")));
        }
        mb.append("```");
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
        //these are more complicated so you should create another class ? extends DynamicMessage in package message.dynamic.general
        //Here is the example class
        //If your dynamic action requires any parameters, you should pass through the constructor.
        return new DynamicTemplate();
    }
}
