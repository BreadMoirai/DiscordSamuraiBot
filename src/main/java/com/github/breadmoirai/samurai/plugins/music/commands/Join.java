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

public class Join extends AbstractMusicCommand {

    @Override
    public void onCommand(CommandEvent event) {
        final VoiceChannel channel;
        if (event.hasContent()) {
            final List<VoiceChannel> voiceChannelsByName = event.getGuild().getVoiceChannelsByName(event.getContent(), true);
            if (voiceChannelsByName.isEmpty()) {
                event.reply("The specified Voice Channel was not found.");
            }
            channel = voiceChannelsByName.get(0);
        } else channel = event.getMember().getVoiceState().getChannel();
        if (channel == null) {
            event.reply("Try joining a voice channel first");
        }
        final Member selfMember = event.getSelfMember();
        if (event.requirePermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK))
            return;
        getPlugin(event).openConnection(channel);
    }
}
