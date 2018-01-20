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
package com.github.breadmoirai.discord.bot.modules.music.command;

import com.github.breadmoirai.breadbot.framework.command.Command;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.discord.bot.modules.music.GuildMusicManager;
import com.github.breadmoirai.discord.bot.modules.music.MusicModule;

import java.util.Optional;

public class PausePlayback {

    @Command
    public void pause(CommandEvent event, MusicModule module) {
        if (setPaused(module, event.getGuildId(), true))
            event.reply("Playback Paused");
    }

    @Command({"unpause", "resume"})
    public void resume(CommandEvent event, MusicModule module) {
        if (setPaused(module, event.getGuildId(), false))
            event.reply("Playback Resumed");
    }

    private boolean setPaused(MusicModule module, long guildId, boolean value) {
        Optional<GuildMusicManager> musicManager = module.retrieveManager(guildId);
        musicManager.ifPresent(guildMusicManager -> guildMusicManager.getPlayer().setPaused(value));
        return musicManager.isPresent();
    }

}
