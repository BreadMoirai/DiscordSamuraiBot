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
package com.github.breadmoirai.bot.modules.points.command;

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.command.ModuleCommand;
import com.github.breadmoirai.bot.modules.points.PointModule;
import com.github.breadmoirai.bot.util.CommandEventUtil;
import net.dv8tion.jda.core.entities.Member;

@Key("points")
public class PointStatus extends ModuleCommand<PointModule> {
    @Override
    public void execute(CommandEvent event, PointModule module) {
        final Member member = CommandEventUtil.getSpecifiedMember(event);
        if (member.equals(event.getMember()))
            event.replyFormat("**%s**, you have **%.2f** points", member.getEffectiveName(), module.getPointSession(member).getPoints());
        else
            event.replyFormat("**%s** has **%.2f** points", member.getEffectiveName(), module.getPointSession(member).getPoints());
    }
}
