/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
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
    private static final Logger INTERNAL_LOGGER;

    static {
        COMMAND_LOGGER = Logger.getLogger("Samurai.CommandLogger");
        INTERNAL_LOGGER = Logger.getLogger("Samurai.InternalLogger");
        final FileHandler handler;
        final FileHandler handler2;
        try {
            handler = new FileHandler("commands.log");
            handler2 = new FileHandler("internal.log");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not start commandLogger");
        }
        handler.setFormatter(new SimpleFormatter());
        handler2.setFormatter(new SimpleFormatter());
        COMMAND_LOGGER.addHandler(handler);
        INTERNAL_LOGGER.addHandler(handler2);
    }


    private MyLogger() {}

    public static void log(String message, Level level, Exception e, CommandContext context) {
        final String msg = message + "\n" + (context == null ? "" : context.toString());
        COMMAND_LOGGER.log(level, msg + e.toString());
    }

    public static void log(String message, Level level, Exception e) {
        INTERNAL_LOGGER.log(level, message, e);
    }
}
