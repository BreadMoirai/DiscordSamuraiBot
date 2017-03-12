package samurai.core.command.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.command.general.Invite;
import samurai.core.entities.base.FixedMessage;
import samurai.core.entities.base.SamuraiMessage;
import samurai.core.entities.dynamic.ExampleMessage;

/**
 * This class defines guidelines to creating a new command
 *
 * @author TonTL
 * @version 4.2
 * @see Command
 * @see ExampleMessage
 * @since 2/16/2017
 */
@Key({"ex", "example", "template"}) //this denotes what command the user should type in ex. "!template"
// you can use an array for aliases ex. @Key({"template", "tp", "temp"})
//keys should be less than 10 characters
@Source // can only be used in DreadMoirai's Samurais;
public class ExampleCommand extends Command {

    /**
     * build your entities here. This is the only method that this class requires.
     *
     * @return a SamuraiMessage that will be passed to the controller to be sent to the originating channel
     * @see Invite A Simple Example
     * @see ExampleMessage an example for dynamic messages
     */
    @Override
    protected SamuraiMessage buildMessage() {
        if (args.contains("simple"))
            //returns a simple text entities
            //use FixedMessage.build("success"); if you just want to acknowledge the user.
            return FixedMessage.build("This is an example message");
        else if (args.contains("dynamic"))
            //returns a dynamic entities
            //see Dynamic Template
            return buildDynamicMessage();
        else
            //builds a fixed entities with all the params
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
        //In order to build your entities, you want to use a MessageBuilder
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
            for (Member u : mentions) mb.append(String.format("%8s. | %s%n", i++ + 1, u.getEffectiveName()));
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
        // the lambda below adds a thumb up emoji to the entities.
        // check using the command `$bin escape` on Discord on how to get the unicode of an emoji
        samuraiMessage.setConsumer(message -> message.addReaction("\uD83D\uDC4D").queue());
        return samuraiMessage;
    }

    /**
     * Builds a dynamic entities that responds to user actions
     *
     * @return a dynamic entities.
     * @see ExampleMessage
     */
    private ExampleMessage buildDynamicMessage() {
        //these are more complicated so you should create another class <? extends DynamicMessage> in package entities.dynamic
        //Here is the example class
        //If your dynamic command requires any parameters, you should pass through the constructor.
        return new ExampleMessage();
    }
}
