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
package com.github.breadmoirai.samurai.command.points;

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import com.github.breadmoirai.samurai.points.PointSession;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

@Key({"give", "donate", "transfer"})
public class Transfer extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final String content = context.getStrippedContent();
        if (CommandContext.isFloat(content)) {
            double value = Double.parseDouble(content);
            final PointSession authorPoints = context.getAuthorPoints();
            if (value <= 0) return null;
            if (authorPoints.getPoints() < value) return FixedMessage.build("You don't have the points!");
            final List<Member> mentionedMembers = context.getMentionedMembers();
            if (mentionedMembers.size() > 0) {
                final Member member = mentionedMembers.get(0);
                context.getPointTracker().offsetPoints(context.getGuildId(), member.getUser().getIdLong(), value);
                authorPoints.offsetPoints(-1 * value);
                return FixedMessage.build(String.format("%s has received your transfer of **%.2f** points.", member.getEffectiveName(), value));
            } else {
                authorPoints.offsetPoints(-1 * value);
                context.getPointTracker().offsetPoints(context.getGuildId(), context.getSelfUser().getIdLong(), value);
                return FixedMessage.build(String.format("Thanks for your donation of **%.2f** points", value));
            }
        }
        return null;
    }
}
