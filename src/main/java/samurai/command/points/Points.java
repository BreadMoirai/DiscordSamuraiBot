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
package samurai.command.points;

import net.dv8tion.jda.core.entities.Member;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.Optional;

@Key("points")
public class Points extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getMentionedMembers().size() > 0) {
            final Member member = context.getMentionedMembers().get(0);
            return FixedMessage.build(String.format("**%s** has **%.2f** points", member.getEffectiveName(), context.getPointTracker().getPoints(context.getGuildId(), member.getUser().getIdLong()).getPoints()));
        } else if (context.hasContent()) {
            final String content = context.getContent().toLowerCase();
            final Optional<Member> any = context.getGuild().getMembers().stream().filter(member1 -> !member1.getUser().isBot()).filter(member1 -> member1.getEffectiveName().toLowerCase().startsWith(content)).findAny();
            if (any.isPresent()) {
                final Member member = any.get();
                return FixedMessage.build(String.format("**%s** has **%.2f** points", member.getEffectiveName(), context.getPointTracker().getPoints(context.getGuildId(), member.getUser().getIdLong()).getPoints()));
            }
        }
        return FixedMessage.build(String.format("**%s**, you have **%.2f** points", context.getAuthor().getEffectiveName(), context.getAuthorPoints().getPoints()));
    }
}
