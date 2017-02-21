package samurai.action.manage;

import samurai.action.Action;
import samurai.annotations.Admin;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.annotations.Listener;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("prefix")
@Listener
@Guild
@Admin
public class Prefix extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        if (args.size() != 1 || args.get(0).length() > 8)
            return FixedMessage.build("Invalid Argument. The prefix must be between 1-8 characters in length. Spaces are not allowed.");
        guild.setPrefix(args.get(0));
        listener.setPrefix(guildId, args.get(0));
        return FixedMessage.build(String.format("Prefix successfully set to `%s`", args.get(0)));
    }
}
