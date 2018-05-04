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

import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Author;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorRoleCommand {

    public static String colorHex(java.awt.Color color) {
        return String.format("0x%6s", Integer.toHexString(color.getRGB() & 0xFFFFFF).toUpperCase()).replace(' ', '0');
    }

    @Command
    public void color(CommandEvent event, Member mention, @Author Member author, Color c) {
        if (mention != null) {
            final java.awt.Color color = mention.getColor();
            event.reply().embed()
                    .appendDescription(mention.getEffectiveName())
                    .appendDescription("'s color is `")
                    .appendDescription(colorHex(color))
                    .appendDescription("`\n")
                    .appendDescription(color.toString())
                    .color(color)
                    .send();
        } else if (c == null) {
            Color color = author.getColor();
            event.reply().embed().appendDescription("Your color is `")
                    .appendDescription(colorHex(color))
                    .appendDescription("`\n")
                    .appendDescription(color.toString())
                    .color(color)
                    .send();
        } else {
            final String colorHex = colorHex(c);
            final String name = "Color: " + colorHex;
            final Guild guild = event.getGuild();
            final List<Role> colorRoleToAdd = guild.getRolesByName(name, false);
            final List<Role> colorRoleToRemove = author.getRoles()
                    .stream()
                    .filter(role -> role.getName().startsWith("Color: "))
                    .collect(Collectors.toList());
            if (!colorRoleToAdd.isEmpty() && colorRoleToRemove.containsAll(colorRoleToAdd)) {
                event.send("You already have this color!");
                return;
            }
            final GuildController guildController = guild.getController();
            if (!colorRoleToAdd.isEmpty()) {
                guildController.modifyMemberRoles(author, colorRoleToAdd, colorRoleToRemove)
                        .queue(aVoid -> deleteEmptyRoles(author, colorRoleToRemove));
            } else {
                guildController.createRole().setName(name).setColor(c).setPermissions(0L).queue(role -> {
                    guildController.modifyRolePositions(false).selectPosition(role).moveTo(2).queue();
                    guildController.modifyMemberRoles(author, Collections.singletonList(role), colorRoleToRemove)
                            .queue(aVoid -> deleteEmptyRoles(author, colorRoleToRemove));
                });
            }
            event.send("Your color has been successfully set to `" + colorHex + "`");
        }
    }

    @Command
    public String uncolor(@Author Member author, Guild guild) {
        final List<Role> colorRoleToRemove = author.getRoles()
                .stream()
                .filter(role -> role.getName().startsWith("Color: "))
                .collect(Collectors.toList());
        guild.getController().removeRolesFromMember(author, colorRoleToRemove).queue();
        guild.getRoles()
                .stream()
                .filter(role -> role.getName().startsWith("Color: "))
                .filter(role -> guild.getMembersWithRoles(role).isEmpty())
                .map(Role::delete)
                .forEach(RestAction::queue);
        return "Your color has been reset to the default color.";

    }

    private void deleteEmptyRoles(Member member, List<Role> rolesRemovedFromMember) {
        for (Role role : rolesRemovedFromMember) {
            final List<Member> membersWithRole = member.getGuild().getMembersWithRoles(role);
            if (membersWithRole.size() == 0) {
                role.delete().queue();
            }
        }
    }
}
