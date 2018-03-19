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
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.samurai.util.ArrayUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.StringJoiner;

public class Ranking {

    private static final int MAX_RESULTS = 10;

    @MainCommand
    public String ranking(CommandEvent context, @Author(unlessMention = true) Member member, DerbyPointPlugin points) {
        final PointSession[] pointSessions = context.getGuild().getMembers().stream().map(Member::getUser).mapToLong(User::getIdLong)
                .mapToObj(points::getPoints).sorted(Comparator.comparingDouble(PointSession::getPoints).reversed()).toArray(PointSession[]::new);
        int target = ArrayUtil.binarySearch(pointSessions, points.getPoints(member.getUser().getIdLong()).getPoints(), PointSession::getPoints, Comparator.naturalOrder(), p -> p.getId() == member.getUser().getIdLong());
        final IntSummaryStatistics intStat = context.getArguments().ints().filter(value -> value > 0 && value <= pointSessions.length).summaryStatistics();
        int idx, end;
        if (intStat.getCount() == 1) {
            end = Math.min(pointSessions.length, intStat.getMax());
            idx = Math.max(0, end - MAX_RESULTS);
        } else if (intStat.getCount() > 1) {
            idx = Math.max(0, intStat.getMin() - 1);
            end = Math.min(pointSessions.length, intStat.getMax());
        } else if (target < MAX_RESULTS) {
            idx = 0;
            end = Math.min(pointSessions.length, MAX_RESULTS);
        } else {
            idx = Math.max(0, target - MAX_RESULTS / 2);
            end = Math.min(pointSessions.length, target + MAX_RESULTS / 2);
        }
        final int length = String.format("%.2f", pointSessions[idx].getPoints()).length();
        final StringJoiner sj = new StringJoiner("\n");
        int ranklen = Math.max(String.valueOf(idx).length(), String.valueOf(end).length());
        final String formatA = String.format("__#`%%-%dd\u00AD`|`\u00AD%%%d.2f` - **%%s**__", ranklen, length);
        final String formatB = String.format("#`%%-%dd\u00AD`|`\u00AD%%%d.2f` - %%s", ranklen, length);
        System.out.println("formatB = " + formatB);
        for (; idx < end; idx++) {
            String s;
            final Member memberById = context.getGuild().getMemberById(pointSessions[idx].getId());
            s = String.format(idx == target ? formatA : formatB, idx + 1, pointSessions[idx].getPoints(), memberById.getEffectiveName());
            if (sj.length() + s.length() >= 2000) {
                break;
            }
            sj.add(s.replace(" ", "\u00AD "));
        }
        return sj.toString();
    }
}
