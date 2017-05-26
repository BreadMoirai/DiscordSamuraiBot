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
package samurai.command.voice;

import ai.api.model.AIContext;
import ai.api.model.AIResponse;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import samurai.audio.AiAPI;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Key("")
public class Chat extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final ArrayList<AIContext> aiContexts = new ArrayList<>();

        final AIContext memberContext = new AIContext("user");
        final HashMap<String, String> memberParams = new HashMap<>();
        memberParams.put("mention", context.getAuthor().getAsMention());
        memberParams.put("name", context.getAuthor().getEffectiveName());
        memberContext.setParameters(memberParams);

        final List<Member> mentionedMembers = context.getMentionedMembers();
        if (!mentionedMembers.isEmpty()) {
            final Member member = mentionedMembers.get(0);
            final AIContext targetContext = new AIContext("target");
            final HashMap<String, String> targetParams = new HashMap<>();
            targetParams.put("mention", member.getAsMention());
            targetParams.put("name", member.getEffectiveName());
            targetContext.setParameters(targetParams);
            aiContexts.add(targetContext);
        }

        final AIResponse query = AiAPI.query(context.getContent(), aiContexts);
        if (query != null) return FixedMessage.build(query.getResult().getFulfillment().getSpeech());
        return null;
    }
}
