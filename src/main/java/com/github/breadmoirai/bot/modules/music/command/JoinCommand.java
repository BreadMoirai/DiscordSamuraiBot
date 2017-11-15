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

import com.github.breadmoirai.bot.modules.music.MusicModule;
import com.github.breadmoirai.bot.util.PermissionFailureResponse;
import com.github.breadmoirai.breadbot.framework.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class JoinCommand {

    @MainCommand
    public void join(CommandEvent event, MusicModule module, VoiceChannel channel) {
        if (channel == null)
            channel = event.getMember().getVoiceState().getChannel();
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
