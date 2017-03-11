package samurai.core.command.util;

import samurai.core.command.Command;
import samurai.core.command.annotations.Client;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.data.SamuraiStore;
import samurai.core.entities.FixedMessage;
import samurai.core.entities.SamuraiMessage;
import samurai.core.entities.dynamic.black_jack.Card;
import samurai.core.entities.dynamic.black_jack.CardFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author TonTL
 * @version 3/7/2017
 */
@Key("draw")
@Source
@Client
public class Draw extends Command {


    @Override
    protected SamuraiMessage buildMessage() {
        if (args.size() == 0) return null;
        if (args.get(0).equals("stack")) {
            ArrayList<Card> cards = new ArrayList<>(args.size() - 1);
            args.stream().skip(1).forEach(s -> {
                try {
                    cards.add(Card.parseCard(s));
                } catch (ParseException ignored) {
                    System.out.println("Parse Error:" + s);
                }
            });
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(CardFactory.drawCardStack(cards), "png", os);
            } catch (IOException e) {
                return FixedMessage.build("Failure to draw Stack.");
            }
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            client.getTextChannelById(String.valueOf(channelId)).sendFile(is, "cardStack235y.png", null).queue();
            return FixedMessage.build("success");
        }
        try {
            Card c;
            c = Card.parseCard(args.get(0));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(CardFactory.drawCard(c), "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            client.getTextChannelById(String.valueOf(channelId)).sendFile(is, c.toString() + ".png", null).queue();
            return FixedMessage.build(c.toString() + ".jpg");
        } catch (ParseException ignored) {
        } catch (IOException e) {
            return FixedMessage.build("Failed to save and upload drawing");
        }
        try {
            client.getTextChannelById(String.valueOf(channelId)).sendFile(generateImageFile(args.get(0)), null).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File generateImageFile(String message) {
        int WIDTH = 100;
        int HEIGHT = 20;
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.drawString(message, 8, 13);
        g.dispose();
        File f = null;
        try { //remove
            f = SamuraiStore.saveToFile(bi, String.valueOf((System.currentTimeMillis() - messageId)).substring(0, 10) + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
