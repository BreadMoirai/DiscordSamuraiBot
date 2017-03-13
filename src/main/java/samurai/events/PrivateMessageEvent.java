package samurai.events;

import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;

/**
 * fired when a message is sent in a private text channel
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class PrivateMessageEvent extends GenericMessageEvent {

    public PrivateMessageEvent(GenericPrivateMessageEvent event) {
        setChannelId(Long.parseLong(event.getChannel().getId()));
        setMessageId(Long.parseLong(event.getMessage().getId()));
        setUserId(Long.parseLong(event.getAuthor().getId()));
        setEdited(event.getMessage().isEdited());
        if (isEdited()) setTime(event.getMessage().getCreationTime());
        else setTime(event.getMessage().getEditedTime());
        setMessage(event.getMessage());
    }
}
