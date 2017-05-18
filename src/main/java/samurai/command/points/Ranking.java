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
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final PointSession[] pointSessions = context.getMemberPoints().toArray(PointSession[]::new);
        int target = 0;
        final List<Member> mentionedMembers = context.getMentionedMembers();
        if (mentionedMembers.size() == 1) {
            target = ArrayUtil.binarySearch(pointSessions, context.getPointTracker().getMemberPointSession(context.getGuildId(), mentionedMembers.get(0).getUser().getIdLong()).getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o));
        } else if (context.hasContent()) {
            final Optional<Member> memberOptional = context.getGuild().getMembers().stream().filter(member -> member.getEffectiveName().toLowerCase().startsWith(context.getContent().toLowerCase())).findAny();
            if (memberOptional.isPresent()) {
                target = ArrayUtil.binarySearch(pointSessions, context.getPointTracker().getMemberPointSession(context.getGuildId(), memberOptional.get().getUser().getIdLong()).getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o));
            }
        }
        if (target == 0)
            target = ArrayUtil.binarySearch(pointSessions, context.getAuthorPoints().getPoints(), PointSession::getPoints, Comparator.comparingDouble(o -> o));
        final IntSummaryStatistics intStat = context.getIntArgs().summaryStatistics();
        int i = 0, limit = 0;
        if (intStat.getCount() == 1) {
            limit = Math.min(pointSessions.length, intStat.getMax());
        } else if (intStat.getCount() > 1){
            i = Math.max(0, intStat.getMin());
            limit = Math.min(pointSessions.length, intStat.getMin());
        } else if (target < 20) {
            i = 0;
            limit = Math.min(pointSessions.length, 20);
        } else {
            i = Math.max(0, target - 10);
            limit = Math.min(pointSessions.length, target + 10);
        }
        final int length = String.format("%.2f", pointSessions[0].getPoints()).length();
        final StringJoiner sj = new StringJoiner("\n");
        for (; i < limit; i++) {
            if (i == target)
                sj.add(String.format(String.format("`\u00AD%%%d.2f` - **%%s**", length), pointSessions[i].getPoints(), pointSessions[i].getMember().getEffectiveName()));
            else
                sj.add(String.format(String.format("`\u00AD%%%d.2f` - %%s", length), pointSessions[i].getPoints(), pointSessions[i].getMember().getEffectiveName()));

        }
        return FixedMessage.build(sj.toString());
    }
}
