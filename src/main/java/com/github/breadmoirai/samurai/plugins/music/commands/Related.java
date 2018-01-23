/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.breadmoirai.samurai.plugins.music.commands;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
import com.github.breadmoirai.samurai.plugins.music.TrackLoader;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;

import java.util.List;
import java.util.Optional;

public class Related {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    @MainCommand
    public TrackLoader related(CommandEvent event, MusicPlugin plugin) {
        if (event.requirePermission(PERMISSIONS)) {
            return null;
        }
        long size = 10L;
        final Optional<GuildAudioManager> managerOptional = plugin.retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            final AudioTrack playingTrack = audioManager.player.getPlayingTrack();
            if (playingTrack != null) {
                final String uri = playingTrack.getInfo().uri;
                if (uri.toLowerCase().contains("youtube")) {
                    final List<String> related = plugin.getRelated(uri.substring(uri.lastIndexOf('=') + 1), size);
                    if (related.isEmpty()) {
                        event.reply("No related tracks exist");
                        return null;
                    }
                    return new TrackLoader(plugin, audioManager, related, String.format("Tracks related to [%s](%s)", playingTrack.getInfo().title, uri));
                } else event.reply("Related tracks are not available for sources other than youtube");
            }
        }
        return null;
    }
}
