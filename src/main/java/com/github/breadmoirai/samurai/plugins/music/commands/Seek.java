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

package com.github.breadmoirai.samurai.plugins.music.commands;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Required;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.util.Arguments;
import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import javax.inject.Inject;
import java.util.Optional;

public class Seek {

    @Inject
    private MusicPlugin plugin;

    @MainCommand
    public void seek(CommandEvent event, @Required String timeTo) {
        final Optional<GuildAudioManager> manager = plugin.retrieveManager(event.getGuildId());
        if (!manager.isPresent()) {
            event.send("Nothing is Playing");
            return;
        }
        final GuildAudioManager gam = manager.get();
        final AudioTrack track = gam.player.getPlayingTrack();
        if (track == null) {
            event.send("Nothing is playing.");
            return;
        }
        final String[] split = timeTo.split(":");
        if (split.length != 2 || !Arguments.isInteger(split[0]) || !Arguments.isInteger(split[1])) {
            event.send("Please provide the time to seek in a [mm:ss] format");
            return;
        }

        if (!track.isSeekable()) {
            event.send("Track is not seekable");
            return;
        }
        track.setPosition((Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1])) * 1000);
    }
}
