package samurai.core.command.util;

import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.entities.FixedMessage;
import samurai.core.entities.SamuraiMessage;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 3/5/2017
 */
@Key("tolist")
@Source
public class ToList extends Command {
    @Override
    protected SamuraiMessage buildMessage() {
        if (args.isEmpty()) return FixedMessage.build("No arguments found");
        else {
            ArrayList<String> list = new ArrayList<>();
            for (String s : args) {
                list.add("\"" + s + "\"");
            }
            return FixedMessage.build("``` \n" + list.toString() + "\n```");
        }

    }
}
