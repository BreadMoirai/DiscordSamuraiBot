package samurai.events.listeners;

import samurai.command.Command;
import samurai.command.CommandFactory;

/**
 * Listens to all commands sent from a channel
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 * @see Command
 * @see CommandFactory
 */
public interface CommandListener extends SamuraiListener {
    void onCommand(Command command);
}
