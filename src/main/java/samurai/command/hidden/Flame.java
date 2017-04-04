package samurai.command.hidden;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.data.SamuraiStore;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

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
public class Flame extends Command {

    private static Random r = new Random();

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        try {
            List<String> flameList = Files.readAllLines(Paths.get(SamuraiStore.class.getResource("./flame.txt").toURI()));
            if (context.getMentionedMembers().isEmpty()) {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", context.getAuthor().getAsMention()));
            } else {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", context.getMentionedMembers().get(0).getAsMention()));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            //todo
            return null;
        }
    }
}
