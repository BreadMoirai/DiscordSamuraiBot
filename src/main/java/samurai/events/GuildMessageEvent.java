package samurai.events;

import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;

/**
 * fired when A message is sent or edited within a guild text channel
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class GuildMessageEvent extends GenericMessageEvent {


    public GuildMessageEvent(GenericGuildMessageEvent event) {
        setChannelId(Long.parseLong(event.getChannel().getId()));
        setMessageId(Long.parseLong(event.getMessage().getId()));
        setUserId(Long.parseLong(event.getAuthor().getId()));
        setEdited(event.getMessage().isEdited());
        if (isEdited()) setTime(event.getMessage().getCreationTime());
        else setTime(event.getMessage().getEditedTime());
        setMessage(event.getMessage());
    }

}
