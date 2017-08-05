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
package com.github.breadmoirai.bot.modules.catdog;

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.command.ModuleMultiCommand;
import net.dv8tion.jda.core.EmbedBuilder;

public class CatDogCommand extends ModuleMultiCommand<CatDogModule> {

    @Key({"cat", "meow"})
    public void meow(CommandEvent event, CatDogModule module) {
        event.reply(new EmbedBuilder().setImage(module.getRandomCatUrl()).build());
    }

    @Key({"dog", "woof"})
    public void woof(CommandEvent event, CatDogModule module) {
        String randomDogUrl = module.getRandomDogUrl();
        while (randomDogUrl.endsWith(".mp4")) {
            randomDogUrl = module.getRandomDogUrl();
        }
        event.reply(new EmbedBuilder().setImage(randomDogUrl).build());
    }
}
