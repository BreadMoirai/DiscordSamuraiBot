package samurai.message;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A message object that has no further options
 * Created by TonTL on 2/13/2017.
 */
public class FixedMessage extends SamuraiMessage {

    private Message message;
    private Consumer<Message> consumer;

    public static FixedMessage build(String s) {
        return new FixedMessage().setMessage(new MessageBuilder().append(s).build());
    }

    public static FixedMessage build(MessageEmbed e) {
        return new FixedMessage().setMessage(new MessageBuilder().setEmbed(e).build());
    }
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
