package com.github.breadmoirai.samurai.plugins.derby;

import com.github.breadmoirai.breadbot.framework.error.BreadBotException;

public class MissingDerbyPluginException extends BreadBotException {

    public MissingDerbyPluginException(String message) {
        super(message);
    }

    public MissingDerbyPluginException() {
        super("The DerbyDatabase Plugin is missing.");
    }
}
