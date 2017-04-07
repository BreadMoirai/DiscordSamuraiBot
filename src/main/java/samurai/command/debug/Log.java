package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.util.MyLogger;

import java.util.logging.Level;

/**
 * @author TonTL
 * @version 5.x - 4/6/2017
 */
@Admin
@Key("log")
public class Log extends Command{

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        MyLogger.log(context.getContent(), Level.SEVERE, new Exception(""), context);
        return null;
    }
}
