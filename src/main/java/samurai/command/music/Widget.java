package samurai.command.music;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4/18/2017
 */
@Key("musicwidget")
public class Widget extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return null;
    }
}
