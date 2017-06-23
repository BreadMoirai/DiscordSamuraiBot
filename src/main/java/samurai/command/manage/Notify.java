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
package samurai.command.manage;

import net.dv8tion.jda.core.entities.Role;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;

import java.util.List;

@Source
@Key("notify")
public class Notify extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.hasContent()) return null;
        Role role = null;
        switch (context.getContent().toLowerCase()) {
            case "roll":
            case "rollpoll":
            case "rp":
            case "daily":
                final List<Role> rollpoll = context.getGuild().getRolesByName("rollpoll", true);
                if (rollpoll.size() > 0) {
                    role = rollpoll.get(0);
                }
            case "qte":
            case "quicktimeevent":
            case "event":
                final List<Role> qte = context.getGuild().getRolesByName("rollpoll", true);
                if (qte.size() > 0) {
                    role = qte.get(0);
                }
        }
        if (role != null) {
            if (!context.getAuthor().getRoles().contains(role)) {
                context.getGuild().getController().addRolesToMember(context.getAuthor(), role).queue();
                context.getChannel().addReactionById(context.getMessageId(), "\uD83D\uDC96").queue();
            } else {
                context.getGuild().getController().removeRolesFromMember(context.getAuthor(), role).queue();
                context.getChannel().addReactionById(context.getMessageId(), "\uD83D\uDC94").queue();
            }
        }
        return null;
    }
}
