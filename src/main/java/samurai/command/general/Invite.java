package samurai.command.general;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

import java.util.List;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("invite")
public class Invite extends Command {

    private static final String INVITE_URL = "https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot&permissions=126016";

    @Override
    public SamuraiMessage execute(CommandContext context) {


        final List<String> args = context.getArgs();
        if (args.size() == 1 && (args.get(0).equalsIgnoreCase("plain") || args.get(0).equalsIgnoreCase("noperm"))) {
            return FixedMessage.build(INVITE_URL.substring(0, 78));
        }
        return FixedMessage.build(INVITE_URL);
    }
}
