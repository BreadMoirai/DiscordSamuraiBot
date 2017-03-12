package samurai.core.events.listeners;

import samurai.core.command.Command;
import samurai.core.command.CommandFactory;

/**
 * Listens to all commands sent from a channel
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 * @see Command
 * @see CommandFactory
 */
public interface CommandListener {
    void onCommand(Command command);
}
