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

import com.github.breadmoirai.bot.modules.music.GuildMusicManager;
import com.github.breadmoirai.bot.modules.music.MusicModule;
import com.github.breadmoirai.bot.util.PermissionFailureResponse;
import com.github.breadmoirai.breadbot.framework.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class History {

    @MainCommand
    public void history(CommandEvent event, MusicModule module) {
        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            event.replyWith(new PermissionFailureResponse(event.getSelfMember(), event.getChannel(), Permission.MESSAGE_EMBED_LINKS));
        }
        final Optional<GuildMusicManager> managerOptional = module.retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildMusicManager manager = managerOptional.get();
            final Deque<AudioTrack> history = manager.getScheduler().getHistory();
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            final AtomicInteger i = new AtomicInteger(0);
            sb.append("**History**");
            history.stream().limit(10).map(track -> MusicModule.trackInfoDisplay(track, true)).map(s -> String.format("\n`%d.` %s", i.incrementAndGet(), s)).forEachOrdered(sb::append);
            event.reply(eb.build());
        }
    }
}
