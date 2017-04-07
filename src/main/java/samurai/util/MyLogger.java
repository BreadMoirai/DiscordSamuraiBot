package samurai.util;

import samurai.command.CommandContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class MyLogger {

    private static final Logger COMMAND_LOGGER;

    static {
        COMMAND_LOGGER = Logger.getLogger("CommandLogger");
        final FileHandler handler;
        try {
            handler = new FileHandler("commands.log");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not start commandLogger");
        }
        handler.setFormatter(new SimpleFormatter());
        COMMAND_LOGGER.addHandler(handler);
    }


    private MyLogger() {}

    public static void log(String message, Level level, Exception e, CommandContext context) {
        final String msg = message + "\n" + context.toString();
        COMMAND_LOGGER.log(level, msg + e.toString());
    }
}
