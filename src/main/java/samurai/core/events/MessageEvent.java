package samurai.core.events;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;

/**
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class MessageEvent extends SamuraiEvent {
    private boolean edited;
    private Message message;

    public MessageEvent(GenericGuildMessageEvent event) {
        setChannelId(Long.parseLong(event.getChannel().getId()));
        setMessageId(Long.parseLong(event.getMessage().getId()));
        setUserId(Long.parseLong(event.getAuthor().getId()));
        edited = event.getMessage().isEdited();
        if (edited) setTime(event.getMessage().getCreationTime());
        else setTime(event.getMessage().getEditedTime());
        message = event.getMessage();
    }

    public boolean isEdited() {
        return edited;
    }

    public Message getMessage() {
        return message;
    }
}
