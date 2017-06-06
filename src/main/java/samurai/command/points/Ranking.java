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
import samurai.points.PointSession;
import samurai.util.ArrayUtil;

import java.util.*;

@Key("ranking")
public class Ranking extends Command {

    private static final int MAX_RESULTS = 10;

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final PointSession[] pointSessions = context.getMemberPoints().toArray(PointSession[]::new);
        int target = -1;
        final List<Member> mentionedMembers = context.getMentionedMembers();
        if (mentionedMembers.size() == 1) {
            target = ArrayUtil.binarySearch(pointSessions, context.getPointTracker().getMemberPointSession(mentionedMembers.get(0)).getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o), pointSession -> pointSession.getMember().equals(mentionedMembers.get(0)));
        } else if (context.hasContent()) {
            final Optional<Member> memberOptional = context.getGuild().getMembers().stream().filter(member -> member.getEffectiveName().toLowerCase().startsWith(context.getContent().toLowerCase())).findAny();
            if (memberOptional.isPresent()) {
                final PointSession memberPointSession = context.getPointTracker().getMemberPointSession(memberOptional.get());
                target = ArrayUtil.binarySearch(pointSessions, memberPointSession.getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o), pointSession -> Objects.equals(memberPointSession, pointSession));
            }
        }
        if (target == -1)
            target = ArrayUtil.binarySearch(pointSessions, context.getAuthorPoints().getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o), pointSession -> context.getAuthor().equals(pointSession.getMember()));
        final IntSummaryStatistics intStat = context.getIntArgs().peek(System.out::println).filter(value -> value > 0 && value <= pointSessions.length).summaryStatistics();
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
            idx = Math.max(0, target - MAX_RESULTS/2);
            end = Math.min(pointSessions.length, target + MAX_RESULTS/2);
        }
        final int length = String.format("%.2f", pointSessions[idx].getPoints()).length();
        final StringJoiner sj = new StringJoiner("\n");
        int ranklen = Math.max(String.valueOf(idx).length(), String.valueOf(end).length());
        for (; idx < end; idx++) {
            String s;
            if (idx == target) {
                s = String.format(String.format("__#`%%-%dd\u00AD`|`\u00AD%%%d.2f` - **%%s**__", ranklen, length), idx + 1, pointSessions[idx].getPoints(), pointSessions[idx].getMember().getEffectiveName());
            } else {
                s = String.format(String.format("#`%%-%dd\u00AD`|`\u00AD%%%d.2f` - %%s", ranklen, length), idx + 1, pointSessions[idx].getPoints(), pointSessions[idx].getMember().getEffectiveName());
            }
            if (sj.length() + s.length() >= 2000) {
                break;
            }
            sj.add(s);
        }
        return FixedMessage.build(sj.toString());
    }
}
