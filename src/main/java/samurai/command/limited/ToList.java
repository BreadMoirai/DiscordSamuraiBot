package samurai.command.limited;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 3/5/2017
 */
@Key("tolist")
@Source
public class ToList extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (context.getArgs().isEmpty()) return FixedMessage.build("No arguments found");
        else {
            ArrayList<String> list = new ArrayList<>();
            for (String s : context.getArgs()) {
                list.add("\"" + s + "\"");
            }
            return FixedMessage.build("``` \n" + list.toString() + "\n```");
        }

    }
}
