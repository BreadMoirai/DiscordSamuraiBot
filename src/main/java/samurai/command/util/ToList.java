package samurai.command.util;

import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

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
