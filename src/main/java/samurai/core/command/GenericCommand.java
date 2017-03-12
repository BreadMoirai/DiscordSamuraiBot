package samurai.core.command;

import samurai.core.entities.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public class GenericCommand extends Command {

    private String key;

    @Override
    protected SamuraiMessage buildMessage() {
        return null;
    }

    public String getKey() {
        return key;
    }

    GenericCommand setKey(String key) {
        this.key = key;
        return this;
    }
}
