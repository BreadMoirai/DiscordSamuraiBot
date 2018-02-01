/*
 *     Copyright 2017-2018 Ton Ly
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
 */
package com.github.breadmoirai.samurai.plugins.groovyval;

import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Content;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Required;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import groovy.lang.Binding;
import org.codehaus.groovy.control.CompilationFailedException;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.github.breadmoirai.samurai.plugins.groovyval.GroovyvalPlugin.getFunctionName;

@com.github.breadmoirai.breadbot.plugins.admin.Admin
public class EvalCommand {

    private static final Pattern JAVA_BLOCK =
            Pattern.compile("([`]{3}(?:java)?[\n])|\n[`]{3}");

    @Command
    public String eval(CommandEvent event, @Content String content, GroovyvalPlugin plugin) {
        setVariables(event, plugin.getBinding());
        if (content == null) return null;
        final StringBuilder sb = new StringBuilder();
        plugin.getImports().forEach(s -> sb.append(s).append("\n"));
        plugin.getFunctions().forEach(s -> sb.append(s).append("\n"));
        final String script = JAVA_BLOCK.matcher(content).replaceAll("");
        sb.append(script);
        final String scriptFull = sb.toString();
        final String result;
        try {
            result = plugin.runScript(scriptFull);
        } catch (CompilationFailedException e) {
            return "```java\n" + e.getMessage() + "```";
        }
        removeVariables(plugin.getBinding());
        return result;
    }

    private void removeVariables(Binding binding) {
        final Map variables = binding.getVariables();
        variables.remove("event");
        variables.remove("author");
        variables.remove("channel");
        variables.remove("category");
        variables.remove("self");
        variables.remove("message");
        variables.remove("guild");
    }

    private void setVariables(CommandEvent event, Binding binding) {
        binding.setVariable("event", event);
        binding.setVariable("author", event.getMember());
        binding.setVariable("channel", event.getChannel());
        binding.setVariable("category", event.getCategory());
        binding.setVariable("self", event.getSelfMember());
        binding.setVariable("message", event.getMessage());
        binding.setVariable("guild", event.getGuild());
    }

    @Command("import")
    public String importCmd(@Content String content, GroovyvalPlugin plugin) {
        String importStatement = "import " + content;
        try {
            plugin.runScript(importStatement);
        } catch (CompilationFailedException e) {
            return "```java\n" + e.getMessage() + "```";
        }
        plugin.getImports().add(importStatement);
        return "Import Added";
    }

    @Command
    public String def(@Required @Content String content, GroovyvalPlugin plugin) {
        final StringBuilder sb = new StringBuilder();
        final String script = JAVA_BLOCK.matcher(content).replaceAll("\n");
        for (String s1 : plugin.getImports()) {
            sb.append(s1).append("\n");
        }
        final List<String> functions = plugin.getFunctions();
        for (String function : functions) {
            if (!getFunctionName(function).equals(getFunctionName(script))) {
                sb.append(function).append("\n");
            }
        }
        try {
            sb.append(script);
            final String scriptFull = sb.toString();
            plugin.runScript(scriptFull);
            functions.removeIf(s -> getFunctionName(s).equals(getFunctionName(script)));
            functions.add(script);
            return "Function Added: " + getFunctionName(script);
        } catch (CompilationFailedException e) {
            return "```java\n" + e.getMessage() + "```";
        }
    }

    @Command
    public void rmfunc(@Required @Content String content, GroovyvalPlugin plugin) {
        plugin.getFunctions().removeIf(s -> getFunctionName(s).equalsIgnoreCase(content));
    }


}
