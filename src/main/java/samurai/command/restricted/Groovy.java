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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Key({"eval", "import", "def", "clear", "delete"})
@Admin
@Creator
public class Groovy extends Command {

    private static final Binding BINDING;
    private static final GroovyShell GROOVY_SHELL;

    private static final Set<String> IMPORTS;
    private static final List<String> FUNCTIONS;

    private static final Pattern FUNCTION_NAME;

    private static final Pattern JAVA_BLOCK;

    static {
        BINDING = new Binding();
        BINDING.setVariable("CREATOR", "DreadMoirai");
        BINDING.setVariable("BOT", Bot.class);
        BINDING.setVariable("STORE", SamuraiStore.class);
        BINDING.setVariable("CF", CommandFactory.class);
        BINDING.setVariable("DB", Database.class);
        BINDING.setVariable("YOUTUBE", YoutubeAPI.class);
        BINDING.setVariable("TRACKER", OsuTracker.class);
        BINDING.setVariable("GAME", Game.class);
        GROOVY_SHELL = new GroovyShell(BINDING);

        IMPORTS = new HashSet<>(20);
        IMPORTS.add("import java.util.*");
        FUNCTIONS = new ArrayList<>(20);
        FUNCTION_NAME = Pattern.compile("[A-Za-z]*[ ]([a-z][A-Za-z]*)\\([A-Za-z\\[\\],<>\\s]*\\)");
        JAVA_BLOCK = Pattern.compile("([`]{3}(?:java)?[\n])|(`)");
    }

    public static void addBinding(String name, Object value) {
        BINDING.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        SamuraiAudioManager.retrieveManager(context.getGuildId()).ifPresent(audioManager -> BINDING.setVariable("audio", audioManager));
        BINDING.setVariable("context", context);
        final String key = context.getKey().toLowerCase();
        final String content = context.getContent();
        switch (key) {
            case "import":
                if (context.hasContent()) {
                    String port = content;
                    if (!content.startsWith("import"))
                        port = "import " + content;
                    try {
                        evalaute(port);
                        IMPORTS.add(port);
                        return FixedMessage.build("Import Added");
                    } catch (Exception e) {
                        return FixedMessage.build(e.getMessage());
                    }
                } else {
                    return FixedMessage.build(IMPORTS.stream().collect(Collectors.joining("\n", "```java\n", "\n```")));
                }
            case "def":
                if (context.hasContent()) {
                    try {
                        final StringBuilder sb = new StringBuilder();
                        IMPORTS.forEach(s -> sb.append(s).append("\n"));
                        FUNCTIONS.forEach(s -> sb.append(s).append("\n"));

                        final String script = JAVA_BLOCK.matcher(content).replaceAll("");
                        sb.append(script);
                        final String scriptFull = sb.toString();
                        evalaute(scriptFull);
                        FUNCTIONS.add(script);
                        return FixedMessage.build("Function Added: " + getFunctionName(script));
                    } catch (Exception e) {
                        return FixedMessage.build(e.getMessage());
                    }
                } else {
                    return FixedMessage.build(FUNCTIONS.stream().map(s -> s.substring(0, s.indexOf('\n'))).collect(Collectors.joining("\n", "```java\n", "\n```")));
                }
            case "clear":
                switch (content.toLowerCase()) {
                    case "import":
                    case "imports":
                        IMPORTS.clear();
                        return FixedMessage.build("Imports cleared");
                    case "function":
                    case "functions":
                        FUNCTIONS.clear();
                        return FixedMessage.build("Functions cleared");
                    default:
                        IMPORTS.clear();
                        FUNCTIONS.clear();
                        return FixedMessage.build("Imports and Functions have been cleared");
                }
            case "delete":
                if (FUNCTIONS.removeIf(s -> getFunctionName(s).equalsIgnoreCase(content)))
                    return FixedMessage.build("Matching function removed");
                else return FixedMessage.build("Specified function not found");
            case "eval":
                try {
                    final StringBuilder sb = new StringBuilder();
                    IMPORTS.forEach(s -> sb.append(s).append("\n"));
                    FUNCTIONS.forEach(s -> sb.append(s).append("\n"));
                    final String script = JAVA_BLOCK.matcher(content).replaceAll("");
                    sb.append(script);
                    final String scriptFull = sb.toString();
                    return evalaute(scriptFull);
                } catch (Exception e) {
                    return FixedMessage.build(e.getMessage());
                }
            default:
                return null;
        }
    }

    private String getFunctionName(String content) {
        final Matcher matcher = FUNCTION_NAME.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private SamuraiMessage evalaute(String script) throws Exception {
        Object result = GROOVY_SHELL.evaluate(script);
        if (result != null) {
            if (result instanceof byte[]) {
                if (((byte[]) result).length == 0) {
                    return FixedMessage.build("Empty byte array");
                }
                return FixedMessage.build(Hex.encodeHexString((byte[]) result));
            }
            return FixedMessage.build(result.toString());
        } else return FixedMessage.build("Null");
    }
}
