/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.github.breadmoirai.bot.util;

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.command.Command;
import com.github.breadmoirai.bot.framework.core.command.Key;

@Key("help")
public class HelpCommand extends Command {
    @Override
    public void execute(CommandEvent event) {
        if (event.hasContent()) {
            final String content = event.getContent();
            event.getClient().getModule(content.toLowerCase()).ifPresent(module -> module.getHelp(event));
        } else {
            event.reply("This is an in development bot. The only currently supported module is `music`. To view detailed help for music command, use `" + event.getPrefix() + "help music`.");
        }
    }

}