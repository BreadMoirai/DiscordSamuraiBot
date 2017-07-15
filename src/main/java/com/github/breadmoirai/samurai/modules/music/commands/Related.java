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

import com.github.breadmoirai.samurai.modules.util.PermissionFailureResponse;
import com.github.breadmoirai.samurai7.core.CommandEvent;
import com.github.breadmoirai.samurai7.core.command.Key;
import com.github.breadmoirai.samurai7.core.command.ModuleCommand;
import com.github.breadmoirai.samurai7.core.response.Response;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai.modules.music.GuildMusicManager;
import com.github.breadmoirai.samurai.modules.music.MusicModule;
import com.github.breadmoirai.samurai.modules.music.TrackLoader;
import com.github.breadmoirai.samurai.modules.music.YoutubeAPI;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;

import java.util.List;
import java.util.Optional;

@Key("related")
public class Related extends ModuleCommand<MusicModule> {

    @Override
    public Response execute(CommandEvent event, MusicModule module) {
        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            return new PermissionFailureResponse(event.getSelfMember(), event.getChannel(), Permission.MESSAGE_EMBED_LINKS);
        }
        final Response failure = module.checkConnection(event, false);
        if (failure != null) return failure;

        final long size = event.hasContent() && event.isNumeric() ? Long.parseLong(event.getContent()) : 10L;

        final Optional<GuildMusicManager> managerOptional = module.retrieveManager(event.getGuildId());
        if (!managerOptional.isPresent()) return null;
        final GuildMusicManager musicManager = managerOptional.get();
        final AudioTrack current = musicManager.getScheduler().getCurrent();
        if (current == null) return Responses.of("There is nothing playing right now.");

        final String uri = current.getInfo().uri;
        if (uri.toLowerCase().contains("youtube")) {
            final List<String> related = YoutubeAPI.getRelated(uri.substring(uri.lastIndexOf('=') + 1), size);
            if (related.isEmpty()) {
                return Responses.of("No related tracks exist");
            }
            String title = event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)
                    ? String.format("Tracks related to [%s](%s)", current.getInfo().title, uri)
                    : "Tracks related to " + current.getInfo().title;
            return new TrackLoader(musicManager, related, title);
        } else return Responses.of("Related tracks are not available for sources other than youtube");
    }
}
