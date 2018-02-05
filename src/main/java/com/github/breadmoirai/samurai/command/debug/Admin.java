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
package com.github.breadmoirai.samurai.command.debug;

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;

import java.util.stream.Collectors;

@Key("admin")
public class Admin extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return FixedMessage.build(context.getGuild().getMembers().stream().filter(member -> !member.getUser().isBot()).filter(member -> member.canInteract(context.getSelfMember()) && member.hasPermission(Permission.KICK_MEMBERS)).map(member1 ->(member1.getOnlineStatus().compareTo(OnlineStatus.INVISIBLE) > 0 ? "~" : "+") + member1.getEffectiveName()).sorted().collect(Collectors.joining("\n", "```diff\n", "\n```")));
    }
}