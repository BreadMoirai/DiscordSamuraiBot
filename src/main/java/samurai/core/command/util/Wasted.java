package samurai.core.command.util;

import samurai.core.Bot;
import samurai.core.command.Command;
import samurai.core.command.annotations.Client;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.data.SamuraiStore;
import samurai.core.entities.FixedMessage;
import samurai.core.entities.SamuraiMessage;
import samurai.util.GifGenerator;
import samurai.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author TonTL
 * @version 3/7/2017
 */
@Key("wasted")
@Source
@Client
public class Wasted extends Command {
    @Override
    protected SamuraiMessage buildMessage() {
        if (mentions.size() != 1) return null;
        String targetUrl = mentions.get(0).getAvatarUrl();
        try {
            boolean spin;
            boolean rave;
            spin = args.contains("spin");
            rave = args.contains("rave");
            boolean grey = args.contains("grey") || args.contains("gs");
            File file = generateImageFile(new URL(targetUrl), spin, rave, grey);
            if (file == null) return null;
            client.getTextChannelById(String.valueOf(channelId)).sendFile(file, null).queue();
        } catch (MalformedURLException e) {
            return FixedMessage.build("Cannot use the default profile picture.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File generateImageFile(URL url, boolean spin, boolean rave, boolean grey) {
        URLConnection connection;
        BufferedImage avatarBI;
        try {
            connection = url.openConnection();
            connection.addRequestProperty("User-Agent", "DiscordApp:270044218167132170");
            avatarBI = ImageIO.read(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        BufferedImage wastedBI = SamuraiStore.getImage("wasted.png");
        int width = avatarBI.getWidth();
        int height = avatarBI.getHeight();
        assert wastedBI != null;


        if (!spin && !rave) {
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = (Graphics2D) result.getGraphics();
            g.drawImage(ImageUtils.greyScale(avatarBI), 0, 0, null);
            g.drawImage(wastedBI, width / 6, height / 6, width * 2 / 3, height * 2 / 3, null);
            g.dispose();
            File f;
            try {
                f = SamuraiStore.saveToFile(result, String.valueOf((System.currentTimeMillis() - messageId)).substring(10) + ".jpg");
            } catch (IOException e) {
                Bot.logError(e);
                return null;
            }
            return f;
        }

        GifGenerator gg = new GifGenerator(avatarBI, wastedBI, String.valueOf((System.currentTimeMillis() - messageId)).substring(10) + ".gif");
        int frames = 60;
        for (String s : args) {
            if (s.contains("frame") || s.contains("f:")) {
                String sc = s.substring(s.indexOf(":") + 1);
                try {
                    frames = Integer.parseInt(sc);
                    break;
                } catch (NumberFormatException e) {
                    break;
                }
            }
        }
        gg.generate(frames, spin, rave, grey);
        return gg.write();
    }
}

