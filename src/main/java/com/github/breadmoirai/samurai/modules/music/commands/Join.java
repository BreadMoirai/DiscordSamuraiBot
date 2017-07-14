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
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai.modules.music.MusicModule;
import com.github.breadmoirai.samurai.util.PermissionFailureResponse;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

@Key("join")
public class Join extends ModuleCommand<MusicModule> {
    @Override
    public com.github.breadmoirai.samurai7.core.response.Response execute(CommandEvent event, MusicModule module) {
        VoiceChannel channel = null;
        if (event.isNumeric()) {
            channel = event.getGuild().getVoiceChannelById(event.getContent());
        }
        if (channel == null && event.hasContent()) {
            final List<VoiceChannel> voiceChannelsByName = event.getGuild().getVoiceChannelsByName(event.getContent(), true);
            if (!voiceChannelsByName.isEmpty())
                channel = voiceChannelsByName.get(0);
        } else channel = event.getMember().getVoiceState().getChannel();
        if (channel == null) {
            return Responses.of("Try joining a voice channel first");
        }
        final Member selfMember = event.getSelfMember();
        if (!selfMember.hasPermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK))
            return new PermissionFailureResponse(selfMember, channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK);

        module.openConnection(channel);
        return null;
    }
}
