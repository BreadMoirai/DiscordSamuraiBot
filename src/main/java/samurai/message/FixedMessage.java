package samurai.message;

import net.dv8tion.jda.core.entities.Message;

/**
 * A message object that has no further options
 * Created by TonTL on 2/13/2017.
 */
public class FixedMessage extends SamuraiMessage {

    private Message message;

    @Override
    public Message getMessage() {
        return message;
    }

    public FixedMessage setMessage(Message message) {
        this.message = message;
        return this;
    }

}
