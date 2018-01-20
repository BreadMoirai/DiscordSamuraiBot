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
package com.github.breadmoirai.discord.bot.modules.points.command;

import com.github.breadmoirai.discord.bot.framework.core.CommandEvent;
import com.github.breadmoirai.discord.bot.framework.core.command.Key;
import com.github.breadmoirai.discord.bot.framework.core.command.ModuleCommand;
import com.github.breadmoirai.discord.bot.modules.points.LevelStatus;
import com.github.breadmoirai.discord.bot.modules.points.PointModule;
import com.github.breadmoirai.discord.bot.util.CommandEventUtil;
import net.dv8tion.jda.core.entities.Member;

@Key("level")
public class Level extends ModuleCommand<PointModule> {

    private static final String FILL = "\u2588", BORDER = "\u2502";

    @Override
    public void execute(CommandEvent event, PointModule module) {
        final Member member = CommandEventUtil.getSpecifiedMember(event);
        final LevelStatus levelStatus = LevelStatus.fromSession(module.getPointSession(member));
        final double progress = levelStatus.getExpProgress() * 100 / 4;
        event.replyFormat("**%s**%n" +
                        "Points:`%.2f`    Exp:`%.2f`%n" +
                        "Level:`%d`                (`%.0f`/`%.0f`)%n" +
                        "`%s%-25s%7$s`",
                member.getEffectiveName(),
                levelStatus.getPoints(), levelStatus.getExp(),
                levelStatus.getLevel(), levelStatus.getExpAtThisLevel(), levelStatus.getTotalExpRequiredForLevel(),
                BORDER, progress == 0 ? "" : String.format("%" + progress + "s", "").replace(" ", FILL));
    }
}
