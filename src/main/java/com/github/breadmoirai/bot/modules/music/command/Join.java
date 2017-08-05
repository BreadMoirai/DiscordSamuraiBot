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

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.command.ModuleCommand;
import com.github.breadmoirai.bot.modules.music.MusicModule;
import com.github.breadmoirai.bot.util.PermissionFailureResponse;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

@Key("join")
public class Join extends ModuleCommand<MusicModule> {
    @Override
    public void execute(CommandEvent event, MusicModule module) {
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
            event.reply("Try joining a voice channel first");
            return;
        }
        final Member selfMember = event.getSelfMember();
        if (!selfMember.hasPermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            event.replyWith(new PermissionFailureResponse(selfMember, channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK));
            return;
        }
        module.openConnection(channel);
    }
}