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

import com.github.breadmoirai.discord.bot.framework.core.CommandEvent;
import com.github.breadmoirai.discord.bot.framework.core.command.Key;
import com.github.breadmoirai.discord.bot.framework.core.command.ModuleCommand;
import com.github.breadmoirai.discord.bot.modules.music.GuildMusicManager;
import com.github.breadmoirai.discord.bot.modules.music.MusicModule;
import com.github.breadmoirai.discord.bot.modules.music.TrackLoader;
import com.github.breadmoirai.discord.bot.modules.music.YoutubeAPI;
import com.github.breadmoirai.discord.bot.util.PermissionFailureResponse;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;

import java.util.List;
import java.util.Optional;

@Key("related")
public class Related extends ModuleCommand<MusicModule> {

    @Override
    public void execute(CommandEvent event, MusicModule module) {
        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            event.replyWith(new PermissionFailureResponse(event.getSelfMember(), event.getChannel(), Permission.MESSAGE_EMBED_LINKS));
            return;
        }
        if (!module.checkConnection(event, false)) return;

        final long size = event.hasContent() && event.isNumeric() ? Long.parseLong(event.getContent()) : 10L;

        final Optional<GuildMusicManager> managerOptional = module.retrieveManager(event.getGuildId());
        if (!managerOptional.isPresent()) return;
        final GuildMusicManager musicManager = managerOptional.get();
        final AudioTrack current = musicManager.getScheduler().getCurrent();
        if (current == null) {
            event.reply("There is nothing playing right now.");
            return;
        }

        final String uri = current.getInfo().uri;
        if (uri.toLowerCase().contains("youtube")) {
            final List<String> related = YoutubeAPI.getRelated(uri.substring(uri.lastIndexOf('=') + 1), size);
            if (related.isEmpty()) {
                event.reply("No related tracks exist");
                return;
            }
            String title = event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)
                    ? String.format("Tracks related to [%s](%s)", current.getInfo().title, uri)
                    : "Tracks related to " + current.getInfo().title;
            event.replyWith(new TrackLoader(musicManager, related, title));
        } else event.reply("Related tracks are not available for sources other than youtube");
    }
}
