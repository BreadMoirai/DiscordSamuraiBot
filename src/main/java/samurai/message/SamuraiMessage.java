package samurai.message;

import net.dv8tion.jda.core.entities.Message;

import java.util.function.Consumer;

/**
 * @author TonTL
 * @since 4.0
 */
public abstract class SamuraiMessage {


    private long channelId;

    /**
     * This is the method that retrieves the message to be sent/updated to.
     *
     * @return the message that will be sent/replace
     */
    public abstract Message getMessage();

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public abstract boolean isPersistent();

    public abstract Consumer<Message> getConsumer();

}
