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
package com.github.breadmoirai.bot.util;

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;
import java.util.Optional;

public class CommandEventUtil {

    public static Member getSpecifiedMember(CommandEvent event) {
        final List<Member> mentionedMembers = event.getMentionedMembers();
        if (mentionedMembers.size() > 0) {
            return mentionedMembers.get(0);
        } else if (event.hasContent()) {
            final String content = event.getContent().toLowerCase();
            final Optional<Member> any = event.getGuild().getMembers().stream().filter(member -> !member.getUser().isBot() || member.equals(event.getSelfMember())).filter(member1 -> member1.getEffectiveName().toLowerCase().startsWith(content) || (member1.getNickname() == null && member1.getUser().getName().toLowerCase().startsWith(content))).findAny();
            return any.orElse(event.getMember());
        }
        return event.getMember();
    }
}
