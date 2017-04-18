package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.FileMessage;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.black_jack.Card;
import samurai.messages.impl.black_jack.CardFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 3/7/2017
 */
@Key("draw")
@Source
public class Draw extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        final List<String> args = context.getArgs();
        if (args.size() == 0) return null;
        if (args.get(0).equals("stack")) {
            List<Card> cards = new ArrayList<>(args.size() - 1);
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
            return new FileMessage(context.getChannelId(), is, cards.stream().map(Card::toString).collect(Collectors.joining()) + ".png", null);
        }
        try {
            Card c;
            c = Card.parseCard(args.get(0));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(CardFactory.drawCard(c), "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return new FileMessage(context.getChannelId(), is, c.toString() + ".png", null);
        } catch (ParseException ignored) {
        } catch (IOException e) {
            return FixedMessage.build("Failed to save and upload drawing");
        }
        try {
            return new FileMessage(context.getChannelId(), textToImage(args.get(0)), args.get(0) + ".png", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream textToImage(String text) throws IOException {
        int WIDTH = 100;
        int HEIGHT = 20;
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.drawString(text, 8, 13);
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
