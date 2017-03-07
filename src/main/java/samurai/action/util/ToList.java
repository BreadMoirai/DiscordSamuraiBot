package samurai.action.util;

import samurai.action.Action;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 3/5/2017
 */
@Key("tolist")
@Source
public class ToList extends Action {
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
