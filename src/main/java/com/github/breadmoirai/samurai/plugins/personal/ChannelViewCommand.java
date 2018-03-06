/*
 *     Copyright 2017-2018 Ton Ly
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
 */

package com.github.breadmoirai.samurai.plugins.personal;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.plugins.admin.Admin;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

public class ChannelViewCommand {

    @MainCommand
    @Admin
    public String viewas(Member member, Role role, Guild guild) {
        if (role != null) {
            final List<Channel> channels = getChannels(guild);
            StringJoiner joiner = getStringJoiner();
            for (Channel channel : channels) {
                String s = channelWRDiff(channel, PermissionUtil.getEffectivePermission(channel, role));
                if (s != null) {
                    joiner.add(s);
                }
            }
            return joiner.toString();
        } else if (member != null) {
            final List<Channel> channels = getChannels(guild);
            StringJoiner joiner = getStringJoiner();
            for (Channel channel : channels) {
                String s = channelWRDiff(channel, PermissionUtil.getEffectivePermission(channel, member));
                if (s != null) {
                    joiner.add(s);
                }
            }
            return joiner.toString();
        } else {
            final List<Channel> channels = getChannels(guild);
            StringJoiner joiner = getStringJoiner();
            for (Channel channel : channels) {
                String s = channelWRDiff(channel,
                                         PermissionUtil.getEffectivePermission(channel, guild.getPublicRole()));
                if (s != null) {
                    joiner.add(s);
                }
            }
            return joiner.toString();
        }
    }

    @NotNull
    private StringJoiner getStringJoiner() {
        StringJoiner joiner = new StringJoiner("\n", "```diff\n", "```");
        joiner.setEmptyValue(
                "```You find yourself in a strange place. You don't have access to any text channels, or there are " +
                        "none in this server.```");
        return joiner;
    }

    @NotNull
    private List<Channel> getChannels(Guild guild) {
        final List<Channel> channels = new ArrayList<>();
        channels.addAll(guild.getVoiceChannels());
        channels.addAll(guild.getTextChannels());
        channels.sort(Comparator.comparingInt(Channel::getPositionRaw));
        return channels;
    }

    @Nullable
    private String channelWRDiff(Channel channel, long perms) {
        if ((Permission.MESSAGE_READ.getRawValue() & perms) == 0)
            return null;
        boolean write = (Permission.MESSAGE_WRITE.getRawValue() & perms) != 0;
        return (write ? "+ " : "- ") + channel.getName();
    }

}
