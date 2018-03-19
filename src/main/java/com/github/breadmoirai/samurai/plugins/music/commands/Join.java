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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;
import java.util.Optional;

public class Join extends AbstractMusicCommand {

    @Override
    public void onCommand(CommandEvent event) {
        final VoiceChannel channel;
        if (event.hasContent()) {
            final Optional<VoiceChannel> voiceChannelsByName = event.getGuild().getVoiceChannels().stream()
                    .filter(voiceChannel -> voiceChannel.getName().toLowerCase().contains(event.getContent().toLowerCase()))
                    .findAny();
            if (!voiceChannelsByName.isPresent()) {
                event.reply("The specified Voice Channel was not found.");
                return;
            }
            channel = voiceChannelsByName.get();
        } else {
            channel = event.getMember().getVoiceState().getChannel();
        }
        if (channel == null) {
            event.reply("Try joining a voice channel first");
            return;
        }
        if (event.requirePermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK))
            return;
        getPlugin(event).openConnection(channel);
    }
}