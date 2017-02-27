package samurai.action.general;

import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.data.Store;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/23/2017
 */
@Key("flame")
@Source
public class Flame extends Action {

    private static Random r;

    static {
        r = new Random();
    }

    @Override
    protected SamuraiMessage buildMessage() {
        try {
            List<String> flameList = Files.readAllLines(Paths.get(Store.class.getResource("./flame.txt").toURI()));
            if (mentions.isEmpty()) {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", author.getAsMention()));
            } else {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", mentions.get(0).getAsMention()));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Bot.logError(e);
            return null;
        }
    }
}
