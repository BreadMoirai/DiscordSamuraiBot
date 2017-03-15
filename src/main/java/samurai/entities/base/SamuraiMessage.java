package samurai.entities.base;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.core.MessageManager;

import java.time.OffsetDateTime;

/**
 * @author TonTL
 * @since 5.0
 */
public abstract class SamuraiMessage {


    private long channelId;
    private TextChannel channel;

    /**
     * This is the method that retrieves the entities to be sent/updated to.
     *
     * @return the entities that will be sent/replace
     */

    public long getChannelId() {
        return channelId;
    }

    public void onReady(MessageManager messageManager) {
        onReady(channel);
    }

    protected abstract void onReady(TextChannel channel);

    public void setChannel(TextChannel channel) {
        this.channel = channel;
        this.channelId = Long.parseLong(channel.getId());
    }

    public TextChannel getChannel() {
        return channel;
    }
}
