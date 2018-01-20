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
 *//*

package net.breadmoirai.samurai.modules.points.command;

import net.breadmoirai.samurai.modules.points.PointModule;
import net.breadmoirai.samurai.modules.points.PointSession;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.command.ModuleCommand;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

@Key({"give", "donate", "transfer"})
public class Transfer extends ModuleCommand<PointModule> {

    @Override
    public Response execute(CommandEvent event, PointModule module) {
        final String content = event.getArgs();
        if (event.isFloat(content)) {
            double value = Double.parseDouble(content);
            final Member author = event.getMember();
            final PointSession authorPoints = module.getPointSession(author);
            if (value <= 0) return null;
            if (authorPoints.getPoints() < value) return Responses.of("You don't have the points!");
            final List<Member> mentionedMembers = event.getMentionedMembers();
            if (mentionedMembers.size() > 0) {
                final Member member = mentionedMembers.get(0);
                module.offsetPoints(member, value);
                module.offsetPoints(author,-1 * value);
                return Responses.ofFormat("%s has received your transfer of **%.2f** points.", member.getEffectiveName(), value);
            } else {
                module.offsetPoints(event.getGuildId(), event.getSelfUser().getIdLong(), value);
                module.offsetPoints(author, -1 * value);
                return Responses.ofFormat("Thanks for your donation of **%.2f** points", value);
            }
        }
        return null;
    }
}
*/
