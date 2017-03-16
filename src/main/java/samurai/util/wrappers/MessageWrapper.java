package samurai.util.wrappers;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class MessageWrapper {


    private Message message;

    MessageWrapper(Message message) {
        this.message = message;
    }

    private void setMessage(Message message) {
        this.message = message;
    }

    public void editMessage(String s) {
        message.editMessage(s).queue(this::setMessage, null);
    }


    public void editMessage(MessageEmbed messageEmbed) {
        message.editMessage(messageEmbed).queue(this::setMessage, null);
    }


    public void editMessage(Message message) {
        this.message.editMessage(message).queue(this::setMessage, null);
    }

    public void appendMessage(String s) {
        message.editMessage(message.getContent() + s).queue(this::setMessage, null);
    }


    public void delete() {
        message.delete().queue(null, null);
    }

    public void addReaction(String... s) {
        for (String value : s) {
            message.addReaction(value).queue(null ,null);
        }
    }

    public void addReaction(Collection<String> s) {
        s.stream().map(message::addReaction).forEach((voidRestAction) -> voidRestAction.queue(null, null));
    }

    public void addReaction(Collection<String> s, Consumer<Void> consumer) {
        final Iterator<String> iterator = s.iterator();
        int i = 0;
        while (i++ != s.size()-1) {
            message.addReaction(iterator.next()).queue(null ,null);
        }
        message.addReaction(iterator.next()).queue(consumer, null);
    }


    public void clearReactions() {
        message.clearReactions().queue(null, null);
    }

    public void removeReaction(MessageReactionAddEvent event)
    {
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    public void editMessage(Message message, Consumer<Message> consumer) {
        this.message.editMessage(message).queue(consumer, null);
    }
    public void editMessage(String message, Consumer<Message> consumer) {
        this.message.editMessage(message).queue(consumer, null);
    }
}
