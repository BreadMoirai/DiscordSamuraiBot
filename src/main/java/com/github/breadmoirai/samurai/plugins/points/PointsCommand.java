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
package com.github.breadmoirai.samurai.plugins.points;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Author;
import net.dv8tion.jda.core.entities.Member;

public class PointsCommand {

    @MainCommand({"points", "point"})
    public String getPoints(@Author(unlessMention = true) Member member, DerbyPointPlugin points) {
        return String.format("**%s** has **%.2f** points", member.getEffectiveName(), points.getPoints(member.getUser().getIdLong()).getPoints());
    }

}
