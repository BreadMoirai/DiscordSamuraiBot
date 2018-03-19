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

import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.samurai.plugins.music.AbstractMusicCommand;
import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/14/2017
 */
public class Previous extends AbstractMusicCommand {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    @Override
    public void onCommand(CommandEvent event) {
        if (event.requirePermission(PERMISSIONS)) {
            return;
        }
        final Optional<GuildAudioManager> managerOptional = getPlugin(event).retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager guildAudioManager = managerOptional.get();
            guildAudioManager.scheduler.prevTrack();
            final AudioTrack currentTrack = guildAudioManager.player.getPlayingTrack();
            final AudioTrackInfo trackInfo = currentTrack.getInfo();
            final EmbedBuilder embedBuilder = new EmbedBuilder();
            final StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
            descriptionBuilder
                    .append(String.format("Playing [`%02d:%02d`/`%02d:%02d`]", currentTrack.getPosition() / (60 * 1000), currentTrack.getPosition() / 1000, trackInfo.length / (60 * 1000), (trackInfo.length / 1000) % 60))
                    .append("\n[").append(trackInfo.title).append("](").append(trackInfo.uri).append(")\n");
            event.reply().setEmbed(embedBuilder.build());
        }
    }
}
