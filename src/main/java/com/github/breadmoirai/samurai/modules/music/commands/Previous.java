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
import com.github.breadmoirai.samurai7.core.command.ModuleCommand;
import com.github.breadmoirai.samurai7.core.response.Response;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai.modules.music.GuildMusicManager;
import com.github.breadmoirai.samurai.modules.music.MusicModule;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Optional;

@Key("previous")
public class Previous extends ModuleCommand<MusicModule> {

    @Override
    public Response execute(CommandEvent event, MusicModule module) {
        final Optional<GuildMusicManager> managerOptional = module.retrieveManager(event.getGuildId());
        final boolean hyperLink = event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS);
        if (managerOptional.isPresent()) {
            final GuildMusicManager guildAudioManager = managerOptional.get();
            guildAudioManager.getScheduler().prevTrack();
            final AudioTrack currentTrack = guildAudioManager.getScheduler().getCurrent();
            final EmbedBuilder embedBuilder = new EmbedBuilder();
            final StringBuilder sb = embedBuilder.getDescriptionBuilder();
            sb.append("Now playing:\n")
                    .append(MusicModule.trackInfoDisplay(currentTrack, true, hyperLink));
            return Responses.of(embedBuilder.build());
        }
        return null;
    }
}
