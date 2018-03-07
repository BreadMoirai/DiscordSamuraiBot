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
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collector;

public class ChannelViewCommand {

    @MainCommand
    @Admin
    public String viewas(Member member, Role role, Guild guild) {
        if (role != null) {
            return guild.getCategories()
                        .stream()
                        .map(Category::getChannels)
                        .flatMap(List::stream)
                        .map(channel -> channelWRDiff(channel, PermissionUtil.getEffectivePermission(channel, role)))
                        .filter(Objects::nonNull)
                        .collect(getCollector());
        } else if (member != null) {
            return guild.getCategories()
                        .stream()
                        .map(Category::getChannels)
                        .flatMap(List::stream)
                        .map(channel -> channelWRDiff(channel, PermissionUtil.getEffectivePermission(channel, member)))
                        .filter(Objects::nonNull)
                        .collect(getCollector());
        } else {
            return guild.getCategories()
                        .stream()
                        .map(Category::getChannels)
                        .flatMap(List::stream)
                        .map(channel -> channelWRDiff(channel, PermissionUtil.getEffectivePermission(channel,
                                                                                                     guild.getPublicRole())))
                        .filter(Objects::nonNull)
                        .collect(getCollector());
        }
    }

    @Nullable
    private String channelWRDiff(Channel channel, long perms) {
        if ((Permission.MESSAGE_READ.getRawValue() & perms) == 0)
            return null;
        boolean write = (Permission.MESSAGE_WRITE.getRawValue() & perms) != 0;
        return (write ? "+ " : "- ") + channel.getName();
    }

    private Collector<CharSequence, ?, String> getCollector() {
        return Collector.of(() -> {
            final StringJoiner sj = new StringJoiner("\n", "```diff\n", "```");
            sj.setEmptyValue(
                    "```You find yourself in a strange place. " +
                            "You don't have access to any text channels, " +
                            "or there are none in this server.```");
            return sj;
        }, StringJoiner::add, StringJoiner::merge, StringJoiner::toString);
    }
}
