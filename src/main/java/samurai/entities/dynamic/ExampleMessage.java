package samurai.entities.dynamic;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import samurai.command.GenericCommand;
import samurai.command.util.ExampleCommand;
import samurai.entities.base.DynamicMessage;
import samurai.events.GuildMessageEvent;
import samurai.events.ReactionEvent;
import samurai.events.listeners.GenericCommandListener;
import samurai.events.listeners.MessageListener;
import samurai.events.listeners.ReactionListener;
import samurai.util.CircularlyLinkedList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple template class to explain functionality of this class
 * A DynamicMessage works on stages
 *
 * @author TonTL
 * @version 5.0
 * @see ExampleCommand
 * @since 3.10.2017
 */
public class ExampleMessage extends DynamicMessage implements ReactionListener, MessageListener, GenericCommandListener {
    //In order for the message to update, it should implement a listener
    //ex. ReactionListener, MessageListener, GenericCommandListener, PrivateListener, CommandListener

    // should probably have one of these, a list of discord emojis;
    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("\uD83D\uDD04", "\uD83D\uDD12"));
    private CircularlyLinkedList<TemplateState> states;

    //this is fired when this object is ready to send and receive events
    @Override
    protected void onReady() {
        //step 1. Build a message
        MessageBuilder mb = new MessageBuilder();
        //add some text
        mb.append("This is a dynamic message");
        //let's build it
        Message m = mb.build();
        //now if we just want to send it back out we can use this.submitNewMessage(m);

        //what if we want to send a message and add reactions?
        //we will need a success consumer. the success consumer is a lambda that activates after the message is sent with the message as it's argument
        submitNewMessage(m, newMenu(REACTIONS));
        //with that the message will be sent and then the reactions will be attached.

        //in order to control the behavior of this let's implement a state pattern
        states = new CircularlyLinkedList<>();
        states.insert(new ReverseState());
        states.insert(new AppendState());
        states.insert(new CopyState());
    }

    @Override
    public void onReaction(ReactionEvent event) {
        //first let's check if the event has the reaction we want
        if (!REACTIONS.contains(event.getName())) return;


        //if we get a reaction circle arrows
        if (event.getName().equals(REACTIONS.get(0))) {
            //let's change the state
            states.advance();
            if (states.current() instanceof AppendState) ((AppendState) states.current()).clear();
            //and remove the reaction so the user can place another reaction if wanted
            removeReaction(event);
        }
        //else if we get the lock reaction
        else if (event.getName().equals(REACTIONS.get(1))) {
            //we'll unregister ourselves and thus no longer listen to any events;
            clearReactions();
            unregister();
            //all references to this class should be lost at this point
        }
    }


    @Override
    public void onMessageEvent(GuildMessageEvent event) {
        //here we'll just delegate our behavior to our state
        states.current().onMessageEvent(event);
    }

    @Override
    public void onCommand(GenericCommand command) {
        //same as above. delegation to states
        states.current().onCommand(command);
    }

    //state pattern classes
    private abstract class TemplateState implements MessageListener, GenericCommandListener {
        //generally we want the state classes to be static and thus not need to reference the enclosing class
        //by making these classes static, we would decouple them which is always better.
    }

    private class CopyState extends TemplateState {

        @Override
        public void onMessageEvent(GuildMessageEvent event) {
            updateMessage("**CopyState**\n" + event.getMessage().getContent());
        }

        @Override
        public void onCommand(GenericCommand command) {
            clearReactions();
            submitNewMessage("**CopyState**\n" + "Key: " + command.getKey(), newMenu(REACTIONS));
        }
    }


    private class ReverseState extends TemplateState {

        @Override
        public void onMessageEvent(GuildMessageEvent event) {
            updateMessage("**ReverseState**\n" + StringUtils.reverse(event.getMessage().getRawContent()));
        }

        @Override
        public void onCommand(GenericCommand command) {
            StringBuilder sb = new StringBuilder().append("**ReverseState**\n");
            command.getArgs().forEach(s -> sb.insert(0, ' ').insert(0, s));
            clearReactions();
            submitNewMessage(sb.toString(), newMenu(REACTIONS));
        }
    }


    private class AppendState extends TemplateState {

        private StringBuilder sb = new StringBuilder();

        @Override
        public void onMessageEvent(GuildMessageEvent event) {
            sb.append(event.getMessage().getContent()).append("\n");
            updateMessage(sb.toString());
        }

        @Override
        public void onCommand(GenericCommand command) {
            sb.append("`").append(command.getKey());
            for (String s : command.getArgs())
                sb.append(' ').append(s);
            sb.append('`').append('\n');
            submitNewMessageAndDeleteCurrent(sb.toString(), newMenu(REACTIONS));
        }

        public void clear() {
            sb = new StringBuilder().append("**AppendState**\n");
        }
    }


}
