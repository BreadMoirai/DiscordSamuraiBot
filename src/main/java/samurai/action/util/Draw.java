package samurai.action.util;

import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.data.SamuraiStore;
import samurai.message.SamuraiMessage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author TonTL
 * @version 3/7/2017
 */
@Key("draw")
@Source
@Client
public class Draw extends Action {


    @Override
    protected SamuraiMessage buildMessage() {
        try {
            client.getTextChannelById(String.valueOf(channelId)).sendFile(generateImageFile(args.get(0)), null).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File generateImageFile(String message) {
        int PIX_SIZE = 5;
        int WIDTH = 100;
        int HEIGHT = 20;
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.drawString(message, 8, 13);
        g.dispose();
        File f = null;
        try {
            f = SamuraiStore.saveToFile(bi, String.valueOf((System.currentTimeMillis() - messageId)).substring(0, 10) + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
