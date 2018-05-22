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

package com.github.breadmoirai.samurai.plugins.controlpanel;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

/**
 * Better as a sealed class
 */
public enum ControlPanelType {
    Role((target, member, enable) -> {
        final Guild guild = member.getGuild();
        final net.dv8tion.jda.core.entities.Role role = guild.getRoleById(target);
        if (role != null) {
            final boolean hasRole = member.getRoles().contains(role);
            if (enable && !hasRole) {
                guild.getController().addSingleRoleToMember(member, role).queue();
            } else if (!enable && hasRole) {
                guild.getController().removeSingleRoleFromMember(member, role).queue();
            }
        }
    }),
    Channel((target, member, enable) -> {
        if (member.hasPermission(Permission.ADMINISTRATOR)) return;
        //check for hierarchy
        final Guild guild = member.getGuild();
        final TextChannel channel = guild.getTextChannelById(target);
        if (channel != null) {
            if (enable && !PermissionUtil.checkPermission(channel, member, Permission.VIEW_CHANNEL)) {
                //if can't see but should see
                final PermissionOverride perms = channel.getPermissionOverride(member);
                // check explicit perms
                if (perms != null) {
                    if (hasView(perms.getDeniedRaw())) {
                        if (perms.getDeniedRaw() == Constants.VIEW && perms.getAllowedRaw() == 0L) {
                            // if explicit perms only deny view
                            // check if roles or global perms also deny view
                            final boolean canView = hasView(getRoleOverrides(member, channel));
                            if (canView) {
                                perms.delete().queue();
                            } else {
                                perms.getManager().grant(Constants.VIEW).queue();
                            }
                        } else {
                            if (hasView(getRoleOverrides(member, channel))) {
                                perms.getManager().clear(Constants.VIEW).queue();
                            } else {
                                perms.getManager().grant(Constants.VIEW).queue();
                            }
                        }
                    }
                } else {
                    channel.createPermissionOverride(member).setAllow(Constants.VIEW).queue();
                }
            } else if (!enable && PermissionUtil.checkPermission(channel, member, Permission.VIEW_CHANNEL)) {
                // if can see but shouldn't
                final PermissionOverride perms = channel.getPermissionOverride(member);
                if (perms == null) {
                    channel.createPermissionOverride(member).setDeny(Constants.VIEW).queue();
                } else {
                    if (perms.getAllowedRaw() == Constants.VIEW && perms.getDeniedRaw() == 0L) {
                        if (!hasView(getRoleOverrides(member, channel))) {
                            perms.delete().queue();
                        } else {
                            perms.getManager().deny(Constants.VIEW).queue();
                        }
                    } else {
                        if (!hasView(getRoleOverrides(member, channel))) {
                            perms.getManager().clear(Constants.VIEW).queue();
                        } else {
                            perms.getManager().deny(Constants.VIEW).queue();
                        }
                    }
                }
            }
        }
    });

    private final ControlPanelOperation operation;

    ControlPanelType(ControlPanelOperation operation) {
        this.operation = operation;
    }

    public static ControlPanelType fromChar(char c) {
        switch (c) {
            case 'R':
                return Role;
            case 'C':
                return Channel;
            default:
                throw new EnumConstantNotPresentException(ControlPanelType.class, "" + c);
        }
    }

    /**
     * Gets effective permissions except for member overrides
     *
     * @param member the member
     * @param channel the channel
     * @return the bit permissions
     */
    private static long getRoleOverrides(Member member, TextChannel channel) {
        PermissionOverride override = channel.getPermissionOverride(member.getGuild().getPublicRole());
        // Copied from PermissionUtil#getExplicitPermission and PermissionUtil#apply
        long allowRaw = 0;
        long denyRaw = 0;
        if (override != null) {
            denyRaw = override.getDeniedRaw();
            allowRaw = override.getAllowedRaw();
        }

        long allowRole = 0;
        long denyRole = 0;
        // create temporary bit containers for role cascade
        for (Role role : member.getRoles()) {
            override = channel.getPermissionOverride(role);
            if (override != null) {
                // important to update role cascade not others
                denyRole |= override.getDeniedRaw();
                allowRole |= override.getAllowedRaw();
            }
        }
        // Override the raw values of public role then apply role cascade
        allowRaw = (allowRaw & ~denyRole) | allowRole;
        denyRaw = (denyRaw & ~allowRole) | denyRole;

        long permission = PermissionUtil.getEffectivePermission(member);
        permission &= ~denyRaw;  //Deny everything that the cascade of roles denied.
        permission |= allowRaw;  //Allow all the things that the cascade of roles allowed
        // The allowed bits override the denied ones!
        return permission;
    }

    private static boolean hasView(long perms) {
        return (perms & Constants.VIEW) == Constants.VIEW;
    }

    public void operate(long target, Member member, boolean enable) {
        operation.operate(target, member, enable);
    }

    private static class Constants {

        private static final long VIEW = Permission.MESSAGE_READ.getRawValue();
    }
}
