package samurai.events.listeners;

import samurai.command.Command;
import samurai.command.GenericCommand;

/**
 * Listens for all commands that are not predefined
 * ex. anything that begins with the guild prefix
 *
 * @author TonTL
 * @version 4.x - 3/10/2017
 * @see GenericCommand
 */
public interface GenericCommandListener extends CommandListener {

    @Override
    default void onCommand(Command command) {
        if (command instanceof GenericCommand) {
            onCommand((GenericCommand) command);
        }
    }

    void onCommand(GenericCommand command);


}