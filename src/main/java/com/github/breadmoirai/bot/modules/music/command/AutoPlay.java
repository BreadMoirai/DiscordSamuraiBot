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
package com.github.breadmoirai.bot.modules.music.command;

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.command.ModuleMultiSubCommand;
import com.github.breadmoirai.bot.framework.core.response.simple.StringResponse;
import com.github.breadmoirai.bot.modules.music.GuildMusicManager;
import com.github.breadmoirai.bot.modules.music.MusicModule;

import java.util.Optional;

@Key( "autoplay")
public class AutoPlay extends ModuleMultiSubCommand<MusicModule> {

    @Key("")
    public void executeDefault(CommandEvent event, MusicModule module) {
        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        guildMusicManager.ifPresent(guildMusicManager1 -> event.replyFormat("AutoPlay is currently `%s`", guildMusicManager1.getScheduler().isAutoPlay() ? "enabled" : "disabled"));
    }


    @Key({"on", "enable"})
    public StringResponse enable(CommandEvent event, MusicModule module) {
        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        if (guildMusicManager.isPresent()) {
            guildMusicManager.get().getScheduler().setAutoPlay(true);
            return event.respond("AutoPlay is now `enabled`");
        }
        return null;
    }

    @Key({"off", "disable"})
    public StringResponse disable(CommandEvent event, MusicModule module) {
        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        if (guildMusicManager.isPresent()) {
            guildMusicManager.get().getScheduler().setAutoPlay(false);
            return event.respond("AutoPlay is now `disabled`");
        }
        return null;
    }

}
