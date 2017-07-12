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
package samurai.command.debug;

import net.dv8tion.jda.core.Permission;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Key("perm")
public class Perm extends Command {

    private static final List<Permission> required = Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY);

    @Override
    public SamuraiMessage execute(CommandContext context) {
        List<Permission> permsNotFound = new ArrayList<>();
        List<Permission> permsFound = context.getGuild().getSelfMember().getPermissions(context.getChannel());
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
        return FixedMessage.build(sb.append("```").toString());
    }
}
