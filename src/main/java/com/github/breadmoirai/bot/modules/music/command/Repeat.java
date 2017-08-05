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

import com.github.breadmoirai.bot.util.PermissionFailureResponse;
import com.github.breadmoirai.bot.modules.music.GuildMusicManager;
import com.github.breadmoirai.bot.modules.music.MusicModule;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.command.ModuleCommand;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Optional;

@Key("repeat")
public class Repeat extends ModuleCommand<MusicModule> {
    @Override
    public Response execute(CommandEvent event, MusicModule module) {
        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            return new PermissionFailureResponse(event.getSelfMember(), event.getChannel(), Permission.MESSAGE_EMBED_LINKS);
        }
        final Optional<GuildMusicManager> managerOptional = module.retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildMusicManager guildAudioManager = managerOptional.get();
            final AudioTrack current = guildAudioManager.getScheduler().getCurrent();
            if (current == null) return Responses.of("There is nothing to repeat");
            final boolean b = guildAudioManager.getScheduler().toggleRepeat();
            if (!b) return Responses.of("Repeat stopped");
            else return Responses.of(new EmbedBuilder().appendDescription(MusicModule.trackInfoDisplay(current, true) + " is now playing on repeat").build());
        }
        return Responses.of("There is nothing to repeat.");
    }
}
