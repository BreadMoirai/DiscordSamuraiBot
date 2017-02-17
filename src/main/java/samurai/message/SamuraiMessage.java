package samurai.message;

import net.dv8tion.jda.core.entities.Message;

/**
 * @author TonTL
 * @since 4.0
 */
public abstract class SamuraiMessage {


    private long channelId;

    public abstract Message getMessage();

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

}
