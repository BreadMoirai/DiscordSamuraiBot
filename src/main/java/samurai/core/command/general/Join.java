package samurai.core.command.general;

import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.entities.base.FixedMessage;
import samurai.core.entities.base.SamuraiMessage;

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
