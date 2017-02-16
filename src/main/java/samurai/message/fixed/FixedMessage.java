package samurai.message.fixed;

import net.dv8tion.jda.core.entities.Message;
import samurai.message.SamuraiMessage;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A message object that has no further options
 * Created by TonTL on 2/13/2017.
 */
public class FixedMessage extends SamuraiMessage {

    private Message message;
    private Consumer<Message> consumer;

    @Override
    public Message getMessage() {
        return message;
    }

    public FixedMessage setMessage(Message message) {
        this.message = message;
        return this;
    }

    public Optional<Consumer<Message>> getConsumer() {
        return Optional.ofNullable(consumer);
    }

    public FixedMessage setConsumer(Consumer<Message> consumer) {
        this.consumer = consumer;
        return this;
    }

}
