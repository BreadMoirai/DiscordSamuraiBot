package samurai.events;

import net.dv8tion.jda.core.entities.Message;

/**
 * Fired when a message is sent or edited
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class GenericMessageEvent extends SamuraiEvent {
    private boolean edited;
    private Message message;

    public boolean isEdited() {
        return edited;
    }

    void setEdited(boolean edited) {
        this.edited = edited;
    }

    public Message getMessage() {
        return message;
    }

    protected void setMessage(Message message) {
        this.message = message;
    }
}
