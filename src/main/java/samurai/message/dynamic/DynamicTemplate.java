package samurai.message.dynamic;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.Bot;
import samurai.action.admin.Template;
import samurai.message.modifier.Reaction;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple template class to explain functionality of this class
 * A DynamicMessage works on stages
 *
 * @author TonTL
 * @version 4.0
 * @see Template
 * @since 2/16/2017
 */
public class DynamicTemplate extends DynamicMessage {

    // should probably have one of these
    private static final List<String> ACCEPTED_REACTIONS = Arrays.asList("☑", "👌", "\uD83D\uDC4E", "👥");

    /**
     * this is the method that retrieves the message to be sent/updated to.
     *
     * @return A Message.
     */
    @Override
    public Message getMessage() {
        // You want to use a MessageBuilder to build your message
        MessageBuilder mb = new MessageBuilder()
                .append("***Stage ")
                .append(getStage())
                .append("/")
                .append(getLastStage())
                .append("***\n");
        // you can use a switch-case to change what you are sending
        // all dynamic messages start at stage 0
        switch (getStage()) {
            case 0:
                mb.append("A Standard Dynamic Message.");
                break;
            case 1:
                mb.append("I'm a monster.");
                break;
            case 2:
                mb.append(getReaction().getUser().getAsMention()).append(" is a monster.");
                break;
            case 4:
                mb.append("Wow. It's a Dynamic Message.");
            case 3:
                mb.append(" Whoops.");
                break;
            case 5:
                mb.append("5/").append(getLastStage()).append("? More like 5/7.");
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                break;
            case 10:
                mb.append("The show's over boys.");
                break;
            default:
                new AccessControlException("This Message should not have been accessed at stage " + getStage()).printStackTrace();
                return null;
        }
        return mb.build();
    }


    /**
     * This is method determines who gets to interact with the message.
     *
     * @param messageReaction This is the messageAction associated with this message. Consists of a reaction.
     * @return true if the Reaction is accepted. false otherwise
     */
    @Override
    public boolean valid(Reaction messageReaction) {
        //Rejects the Reaction if it is not within ACCEPTED_REACTIONS
        // You should always have an initialization stage where no input is accepted.
        // here the initialization stage is 0 check DynamicTemplate#GetConsumer
        return getStage() != 0 && getStage() != getLastStage() && ACCEPTED_REACTIONS.contains(messageReaction.getName());
    }

    /**
     * This method changes the message based on the current Reaction
     */
    @Override
    protected void execute() {
        //this method runs every time a new Reaction is detected and validated through valid(Reaction)
        //get the current Reaction
        Reaction action = getReaction();
        switch (action.getName()) {
            //these two reactions will increment the stage by 1
            case "☑":
            case "👌":
                setStage(getStage() + 1);
                break;
            //a thumbs down will decrement the stage
            case "\uD83D\uDC4E":
                if (getStage() > 1) setStage(getStage() - 1);
                break;
            //a busts in silhouette will double the stage id stage < 5
            case "👥":
                if (getStage() <= 5) setStage(getStage() * 2);
                break;
            default:
                Bot.logError(new AccessControlException(String.format("Invalid reaction allowed access to execute%n%s from %s", action.getName(), action.getUser().getName())));
        }
    }

    /**
     * provide an optional consumer
     *
     * @return a consumer to edit message after sending
     */
    @Override
    public Consumer<Message> getConsumer() {

        switch (getStage()) {
            case 0:
                // this is the initialization stage
                // you can use this method to pre-add reactions to message
                // this method will also increment the stage by 1
                return getInitialConsumer(ACCEPTED_REACTIONS);
            default: //getEditConsumer will automatically delete the users reaction
                return getEditConsumer();
        }
    }

    /**
     * marks the final form of the dynamic message
     *
     * @return the stage where no further inputs are taken
     */
    @Override
    protected int getLastStage() {
        return 10;
    }
}
