package samurai.command.general;

import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("join")
public class Join extends Command {

    @Override
    public SamuraiMessage buildMessage() {
        return FixedMessage.build("https://discord.gg/yAMdGU9");
    }
}
