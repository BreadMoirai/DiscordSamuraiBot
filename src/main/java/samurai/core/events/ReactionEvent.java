package samurai.core.events;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.time.OffsetDateTime;

/**
 * @author TonTL
 * @since 4.0
 */
public class ReactionEvent extends SamuraiEvent {

    private String name;

    public ReactionEvent(MessageReactionAddEvent event) {
        setTime(OffsetDateTime.now());
        setChannelId(Long.parseLong(event.getChannel().getId()));
        setMessageId(Long.parseLong(event.getMessageId()));
        setUserId(Long.parseLong(event.getUser().getId()));
        setName(event.getReaction().getEmote().getName());
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }
}
