package samurai.events;

import samurai.command.GenericCommand;

/**
 * @author TonTL
 * @version 3/15/2017
 */
public interface GenericCommandListener extends SamuraiListener {
    void onCommand(GenericCommand command);
}
