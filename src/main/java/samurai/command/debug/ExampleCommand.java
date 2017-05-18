/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.command.debug;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.command.general.Invite;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.impl.util.ExampleMessage;

import java.util.List;

/**
 * This class defines guidelines to creating a new command
 *
 * @author TonTL
 * @version 4.2
 * @see Command
 * @see ExampleMessage
 * @since 2/16/2017
 */
@Key("example") //this denotes what command the user should type in ex. "!template"
// you can use an array for aliases ex. @Key({"template", "tp", "temp"})
//keys should be less than 10 characters
@Source // can only be used in DreadMoirai's Samurais;
public class ExampleCommand extends Command {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};

    /**
     * build your messages here. This is the only method that this class requires.
     *
     * @return a SamuraiMessage that will be passed to the controller to be sent to the originating channel
     * @see Invite A Simple Example
     * @see ExampleMessage an example for dynamic messages
     */
    @Override
    public SamuraiMessage execute(CommandContext context) {
        final List<String> args = context.getArgs();
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        if (args.contains("simple"))
            //returns a simple text messages
            //use FixedMessage.build("success"); if you just want to acknowledge the user.
            return FixedMessage.build("This is an example message");
        else if (args.contains("dynamic"))
            //returns a dynamic messages
            //see Dynamic Template
            return buildDynamicMessage();
        else
            //builds a fixed messages with all the params
            //see below
            return buildFixedMessage(context);

    }

    /**
     * FixedMessages are a type of SamuraiMessage that is not intended to be persistent.
     * These will be sent once and forgotten
     *
     * @see FixedMessage
     */
    private FixedMessage buildFixedMessage(CommandContext context) {
        FixedMessage samuraiMessage = new FixedMessage();
        //In order to build your messages, you want to use a MessageBuilder
        MessageBuilder mb = new MessageBuilder();
        mb.append("This is a FixedMessage.\n");
        mb.append("```\n");
        //This Action object has several fields that you can access
        mb.append(String.format("%-10s| %s%n", "author", context.getAuthor().getUser().getName()))
                .append(String.format("%-10s| %s%n", "channelId", context.getChannelId()))
                .append(String.format("%-10s| %s%n", "content", "----"))
                .append(context.getContent())
                .append(String.format("%-10s| %s%n", "stripped", "----"))
                .append(context.getStrippedContent());
        //get the mentions
        if (!context.getMentionedMembers().isEmpty()) {
            int i = 0;
            mb.append(String.format("%-10s| %s%n", "mentions", "----"));
            for (Member u : context.getMentionedMembers())
                mb.append(String.format("%8s. | %s%n", i++ + 1, u.getEffectiveName()));
        }
        //get the args
        if (!context.getArgs().isEmpty()) {
            int i = 0;
            mb.append(String.format("%-10s| %s%n", "arguments", "----"));
            for (String arg : context.getArgs()) mb.append(String.format("%8s. | %s%n", i++ + 1, arg.replace("\n", "[\\n]")));
        }
        mb.append("```");
        //Now that you have a message built, simply add it to the FixedMessage
        samuraiMessage.setMessage(mb.build());

        // If you want to get a bit more complicated you can add a consumer to your samuraiMessage.
        // the lambda below adds a thumb up emoji to the messages.
        // check using the command `$bin escape` on Discord on how to get the unicode of an emoji
        samuraiMessage.setConsumer(message -> message.addReaction("\uD83D\uDC4D").queue());
        return samuraiMessage;
    }

    /**
     * Builds a dynamic messages that responds to user actions
     *
     * @return a dynamic messages.
     * @see ExampleMessage
     */
    private ExampleMessage buildDynamicMessage() {
        //these are more complicated so you should create another class <? extends DynamicMessage> in package messages.dynamic
        //Here is the example class
        //If your dynamic command requires any parameters, you should pass through the constructor.
        return new ExampleMessage();
    }
}
