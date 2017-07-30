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
package net.breadmoirai.samurai.modules.points.command;

import net.breadmoirai.samurai.modules.points.PointModule;
import net.breadmoirai.samurai.modules.points.PointSession;
import net.breadmoirai.samurai.util.ArraySearch;

import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.command.ModuleCommand;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.dv8tion.jda.core.entities.Member;

import java.util.*;
import java.util.stream.IntStream;

@Key("ranking")
public class Ranking extends ModuleCommand<PointModule> {

    private static final int MAX_RESULTS = 10;

    @Override
    public Response execute(CommandEvent event, PointModule module) {
        final PointSession[] pointSessions = event.getGuild().getMembers().stream()
                .filter(member -> !(member.getUser().isBot() || member.getUser().isFake())
                                || member.getUser().equals(event.getSelfUser()))
                .map(module::getPointSession)
                .sorted(Comparator.comparingDouble(PointSession::getPoints).reversed()).toArray(PointSession[]::new);
        int target = -1;
        final List<Member> mentionedMembers = event.getMentionedMembers();
        if (mentionedMembers.size() == 1) {
            final Member member = mentionedMembers.get(0);
            target = ArraySearch.binarySearch(pointSessions,module.getPointSession(member).getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o), pointSession -> pointSession.getUserId() == member.getUser().getIdLong());
        } else if (event.hasContent()) {
            final Optional<Member> memberOptional = event.getGuild().getMembers().stream().filter(member -> member.getEffectiveName().toLowerCase().startsWith(event.getContent().toLowerCase())).findAny();
            if (memberOptional.isPresent()) {
                final PointSession memberPointSession = module.getPointSession(memberOptional.get());
                target = ArraySearch.binarySearch(pointSessions, memberPointSession.getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o), pointSession -> Objects.equals(memberPointSession, pointSession));
            }
        }
        if (target == -1)
            target = ArraySearch.binarySearch(pointSessions, module.getPointSession(event.getMember()).getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o), pointSession -> event.getAuthorId() == pointSession.getUserId());
        final IntStream intArgs = event.getIntArgs();
        System.out.println("intArgs = " + intArgs);
        final IntSummaryStatistics intStat = intArgs.peek(System.out::println).filter(value -> value > 0 && value <= pointSessions.length).summaryStatistics();
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
        for (int i = idx; i < end; i++) {
            String s;
            final PointSession pointSession = pointSessions[i];
            final Member member = event.getGuild().getMemberById(pointSession.getUserId());
            final String name = member != null ? member.getEffectiveName() : "????";
            if (i == target) {
                s = String.format(formatA, i + 1, pointSession.getPoints(), name);
            } else {
                s = String.format(formatB, i + 1, pointSession.getPoints(), name);
            }
            if (sj.length() + s.length() >= 2000) {
                break;
            }
            sj.add(s.replace(" ", "\u00AD "));
        }
        return Responses.of(sj.toString());
    }
}
