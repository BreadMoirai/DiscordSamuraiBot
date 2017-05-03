/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.command.restricted;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.core.entities.Game;
import org.apache.commons.codec.binary.Hex;
import samurai.Bot;
import samurai.audio.SamuraiAudioManager;
import samurai.audio.YoutubeAPI;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.files.SamuraiStore;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;
import samurai.osu.tracker.OsuTracker;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author TonTL
 * @version 5.0
 * @since 2/18/2017
 */
@Key({"groovy"})
@Admin
@Creator
public class Groovy extends Command {

    private static final Binding BINDING;
    private static final GroovyShell GROOVY_SHELL;

    static {
        BINDING = new Binding();
        BINDING.setVariable("CREATOR", "DreadMoirai");
        BINDING.setVariable("BOT", Bot.class);
        BINDING.setVariable("STORE", SamuraiStore.class);
        BINDING.setVariable("CF", CommandFactory.class);
        BINDING.setVariable("DB", Database.class);
        BINDING.setVariable("YT", YoutubeAPI.class);
        BINDING.setVariable("tracker", OsuTracker.class);
        BINDING.setVariable("Game", Game.class);
        GROOVY_SHELL = new GroovyShell(BINDING);

    }

    public static void addBinding(String name, Object value) {
        BINDING.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        SamuraiAudioManager.retrieveManager(context.getGuildId()).ifPresent(audioManager -> BINDING.setVariable("audio", audioManager));
        final String content = context.getContent().replaceAll("`", "");
        if (content.length() <= 1) return null;
        BINDING.setVariable("context", context);
        if (content.contains("binding")) {
            final Set set = BINDING.getVariables().entrySet();
            if (set.toArray() instanceof Map.Entry[]) {
                Map.Entry[] entryArray = (Map.Entry[]) set.toArray();
                return FixedMessage.build(Arrays.stream(entryArray).map(entry -> entry.getKey().toString() + '=' + entry.getValue().getClass().getSimpleName()).collect(Collectors.joining("\n")));
            }
        }

        try {
            Object result = GROOVY_SHELL.evaluate(content);
            if (result != null) {
                if (result instanceof byte[]) {
                    if (((byte[]) result).length == 0) {
                        return FixedMessage.build("Empty byte array");
                    }
                    return FixedMessage.build(Hex.encodeHexString((byte[]) result));
                }
                return FixedMessage.build("`" + result.toString() + "`");
            } else return FixedMessage.build("Null");
        } catch (Exception e) {
            return FixedMessage.build(e.getMessage());
        }
    }

}
