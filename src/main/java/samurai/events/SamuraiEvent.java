package samurai.events;

import java.time.OffsetDateTime;

/**
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class SamuraiEvent {
    private long userId;
    private long messageId;
    private long channelId;
    private OffsetDateTime time;

    public long getUserId() {
        return userId;
    }

    protected void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMessageId() {
        return messageId;
    }

    void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getChannelId() {
        return channelId;
    }

    protected void setChannelId(long channelId) {
        this.channelId = channelId;
    }


    public OffsetDateTime getTime() {
        return time;
    }

    void setTime(OffsetDateTime time) {
        this.time = time;
    }
}
