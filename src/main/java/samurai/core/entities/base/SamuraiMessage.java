package samurai.core.entities.base;

import samurai.core.MessageManager;

/**
 * @author TonTL
 * @since 5.0
 */
public abstract class SamuraiMessage {


    private long channelId;

    /**
     * This is the method that retrieves the entities to be sent/updated to.
     *
     * @return the entities that will be sent/replace
     */

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public abstract void onReady(MessageManager manager);

}
