package samurai.message.dynamic.black_jack;

import net.dv8tion.jda.core.entities.Message;
import samurai.message.DynamicMessage;
import samurai.message.modifier.Reaction;

import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 3/7/2017
 */
public class BlackJack extends DynamicMessage {


    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    protected boolean valid(Reaction action) {
        return false;
    }

    @Override
    protected void execute(Reaction action) {

    }

    @Override
    public Consumer<Message> createConsumer() {
        return null;
    }

    @Override
    protected int getLastStage() {
        return 0;
    }
}
