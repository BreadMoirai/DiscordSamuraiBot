package samurai.core.entities.base;

import net.dv8tion.jda.core.entities.Message;
import samurai.core.MessageManager;

import java.io.InputStream;

/**
 * @author TonTL
 * @version 4.x - 3/11/2017
 */
public class FileMessage extends SamuraiMessage {

    private long channelId;
    private InputStream data;
    private Message message;
    private String fileName;

    public FileMessage(long channelId, InputStream data, String fileName, Message message) {
        this.channelId = channelId;
        this.data = data;
        this.fileName = fileName;
        this.message = message;
    }

    @Override
    public void onReady(MessageManager manager) {
        manager.submit(channelId, data, fileName, message);
    }
}
