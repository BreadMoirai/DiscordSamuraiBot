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
import com.github.breadmoirai.bot.modules.music.TrackLoader;
import com.github.breadmoirai.breadbot.framework.Response;
import com.github.breadmoirai.breadbot.framework.command.Command;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaySong {

    @Command({"nowplaying", "np"})
    public Response nowPlaying(CommandEvent event, MusicModule module) {
        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            return new PermissionFailureResponse(event.getSelfMember(), event.getChannel(), Permission.MESSAGE_EMBED_LINKS);
        }

        Optional<GuildMusicManager> guildMusicManager = module.retrieveManager(event.getGuildId());
        if (!guildMusicManager.isPresent()) return Responses.of("Nothing is playing right now.");
        final GuildMusicManager audioManager = guildMusicManager.get();
        final AudioTrack currentTrack = audioManager.getScheduler().getCurrent();
        if (currentTrack == null)
            return Responses.of("Nothing is playing right now.");

        final EmbedBuilder eb = new EmbedBuilder();
        final StringBuilder sb = eb.getDescriptionBuilder();

        sb.append(String.format("Now Playing [`%02d:%02d`/`%02d:%02d`]", currentTrack.getPosition() / (60 * 1000), (currentTrack.getPosition() / 1000) % 60, currentTrack.getDuration() / (60 * 1000), (currentTrack.getDuration() / 1000) % 60));
        sb.append("\n[").append(currentTrack.getInfo().title).append("](").append(currentTrack.getInfo().uri).append(")");
        final String userData = currentTrack.getUserData(String.class);
        if (userData != null)
            sb.append(" `").append(userData).append("`\n");
        else sb.append('\n');
        final Collection<AudioTrack> tracks = audioManager.getScheduler().getQueue();
        if (!tracks.isEmpty()) {
            sb.append("Up Next:");
            final AtomicInteger i = new AtomicInteger();
            tracks.stream().limit(10).map(audioTrackInfo -> String.format("%n`%d.` %s", i.incrementAndGet(), MusicModule.trackInfoDisplay(audioTrackInfo, true))).forEachOrdered(sb::append);
            final int tSize = tracks.size();
            if (tSize > 10) {
                sb.append("\n... `").append(tSize - 10).append("` more tracks");
            }
        }
        return Responses.of(eb.build());
    }

    @Command({"queue", "play"})
    public Response queue(CommandEvent event, MusicModule module) {
        if (!event.hasContent())
            return nowPlaying(event, module);
        final Response failure = module.checkConnection(event, true);
        if (failure != null) return failure;

        final Optional<GuildMusicManager> musicManagerOpt = module.retrieveManager(event.getGuildId());
        if (!musicManagerOpt.isPresent()) return Responses.of("Failed to connect. Please try again.");

        final boolean lucky = event.getKey().equalsIgnoreCase("play");

        final GuildMusicManager audioManager = musicManagerOpt.get();
        final String asUrl = getAsUrl(event.getContent());
        if (asUrl != null) {
            return new TrackLoader(audioManager, lucky, asUrl);
        }
        String request = event.getContent();
        if (request.startsWith("yt ")) {
            request = "ytsearch:" + request.substring(3);
        } else if (request.startsWith("sc ")) {
            request = "scsearch:" + request.substring(3);
        }
        if (!request.startsWith("ytsearch:") && !request.startsWith("scsearch:")) {
            request = "ytsearch: " + request;
        }
        return new TrackLoader(audioManager, lucky, request);

    }

    private static final Pattern URL = Pattern.compile("(?:<)?((?:http(s)?://.)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b(?:[-a-zA-Z0-9@:%_+.~#?&/=]*))(?:>)?");

    /**
     * @return the url if found, null if content is not a url.
     */
    private static String getAsUrl(String content) {
        final Matcher matcher = URL.matcher(content);
        if (matcher.matches()) {
            return matcher.group(1);
        } else return null;
    }

}

