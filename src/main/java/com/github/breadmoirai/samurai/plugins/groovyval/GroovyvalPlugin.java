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

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.codec.binary.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GroovyvalPlugin implements CommandPlugin, EventListener {
    private static final Pattern FUNCTION_NAME =
            Pattern.compile("[A-Za-z]*[ ]([a-z][A-Za-z]*)\\([A-Za-z\\[\\],<>\\s]*\\)");

    private final Binding binding;
    private final GroovyShell groovyShell;
    private final Set<String> imports;
    private final List<String> functions;

    public GroovyvalPlugin() {
        binding = new Binding();
        groovyShell = new GroovyShell(binding);

        imports = new HashSet<>(20);
        initializeImports();
        functions = new ArrayList<>(20);
        initializeFunctions();
    }

    public static String getFunctionName(String content) {
        final Matcher matcher = FUNCTION_NAME.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @Override
    public void initialize(BreadBotBuilder builder) {
        builder.addCommand(EvalCommand::new);
    }

    @Override
    public void onBreadReady(BreadBot client) {
        binding.setVariable("bread", client);
        final List<CommandPlugin> plugins = client.getPlugins();
        binding.setVariable("plugins", plugins);
    }

    @SubscribeEvent
    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            binding.setVariable("jda", event.getJDA());
            event.getJDA().removeEventListener(this);
        }
    }

    public String runScript(String script) throws Exception {

        final Object result = groovyShell.evaluate(script);
        if (result != null) {
            if (result instanceof byte[]) {
                if (((byte[]) result).length == 0) {
                    return "Empty byte array";
                }
                return Hex.encodeHexString((byte[]) result);
            } else if (result instanceof int[]) {
                return Arrays.toString(((int[]) result));
            } else if (result instanceof double[]) {
                return Arrays.toString(((double[]) result));
            } else if (result instanceof Object[]) {
                return Arrays.toString(((Object[]) result));
            }
            return result.toString();
        } else return "null";
    }

    private void initializeImports() {
        final InputStream resourceAsStream = GroovyvalPlugin.class.getResourceAsStream("imports.txt");
        if (resourceAsStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
                imports.addAll(br.lines().collect(Collectors.toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeFunctions() {
        final InputStream resourceAsStream = GroovyvalPlugin.class.getResourceAsStream("functions.txt");
        if (resourceAsStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
                functions.addAll(Arrays.asList(br.lines().collect(Collectors.joining()).split("(?:});\n\n")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getImports() {
        return imports;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public Binding getBinding() {
        return binding;
    }
}
