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
package com.github.breadmoirai.samurai.modules.music.commands;

import com.github.breadmoirai.samurai7.core.CommandEvent;
import com.github.breadmoirai.samurai7.core.command.Key;
import com.github.breadmoirai.samurai7.core.command.ModuleMultiSubCommand;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai7.core.response.simple.BasicResponse;
import com.github.breadmoirai.samurai.modules.music.GuildMusicManager;
import com.github.breadmoirai.samurai.modules.music.MusicModule;

import java.util.Optional;

@Key("autoplay")
public class AutoPlay extends ModuleMultiSubCommand<MusicModule> {

    @Key("")
    public BasicResponse executeDefault(CommandEvent event, MusicModule module) {
        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        return guildMusicManager.map(guildMusicManager1 -> Responses.ofFormat("AutoPlay is currently `%s`", guildMusicManager1.getScheduler().isAutoPlay() ? "enabled" : "disabled")).orElse(null);
    }


    @Key({"on", "enable"})
    public BasicResponse enable(CommandEvent event, MusicModule module) {
        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        if (guildMusicManager.isPresent()) {
            guildMusicManager.get().getScheduler().setAutoPlay(true);
            return Responses.of("AutoPlay is now `enabled`");
        }
        return null;
    }

    @Key({"off", "disable"})
    public BasicResponse disable(CommandEvent event, MusicModule module) {
        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        if (guildMusicManager.isPresent()) {
            guildMusicManager.get().getScheduler().setAutoPlay(false);
            return Responses.of("AutoPlay is now `disabled`");
        }
        return null;
    }

}
