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
package com.github.breadmoirai.samurai.modules.utility;

import com.github.breadmoirai.bot.framework.event.CommandEvent;
import net.dv8tion.jda.core.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionCheck {

    private static final List<Permission> required = Arrays.asList(Permission.values());

    public void perm(CommandEvent event) {
        List<Permission> permsNotFound = new ArrayList<>();
        List<Permission> permsFound = event.getGuild().getSelfMember().getPermissions(event.getChannel());
        for (Permission p : required)
            if (!permsFound.contains(p))
                permsNotFound.add(p);
        StringBuilder sb = new StringBuilder().append("```diff\n");
        for (Permission p : required)
            if (permsFound.contains(p))
                sb.append("+ ").append(p).append("\n");
        sb.append("--- ").append("\n");
        for (Permission p : permsNotFound)
            sb.append("- ").append(p).append("\n");
        sb.append("--- ").append("\n");
        for (Permission p : permsFound)
            if (!required.contains(p))
                sb.append("~ ").append(p).append("\n");
        event.reply(sb.toString());
    }

}