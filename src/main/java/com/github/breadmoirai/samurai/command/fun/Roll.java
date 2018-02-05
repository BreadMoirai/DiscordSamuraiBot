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
package com.github.breadmoirai.samurai.command.fun;

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Admin;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.command.manage.Schedule;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import com.github.breadmoirai.samurai.plugins.rollpoll.RollPollMessage;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Key({"roll", "rollpoll"})
@Admin
public class Roll extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getKey().equalsIgnoreCase("rollpoll")) {
            final List<String> args = context.getArgs();
            final Duration duration = Schedule.getDuration(args);
            long pointValue = 0;
            if (args.size() != 0 && CommandContext.isNumber(args.get(0))) {
                pointValue = Long.parseLong(args.get(0));
            }
            if (!duration.isZero())
                if (context.getAuthor().canInteract(context.getSelfMember()))
                    return new RollPollMessage(duration.getSeconds(), TimeUnit.SECONDS, pointValue, pointValue > 0 ? context.getPointTracker() : null, );
                else
                    return new RollPollMessage(duration.getSeconds(), TimeUnit.SECONDS, -1, null, );
            return new RollPollMessage();
        }
        if (context.hasContent()) {
            long limit;
            if (context.isNumeric() && ((limit = Long.parseLong(context.getContent())) > 0)) {
                return FixedMessage.build("**" + context.getAuthor().getEffectiveName() + "** rolls " + ThreadLocalRandom.current().nextLong(limit));
            } else {
                return FixedMessage.build("**" + context.getAuthor().getEffectiveName() + "** rolls " + ((int) context.getContent().codePoints().map(Integer::reverseBytes).map(Integer::reverse).map(Math::abs).map(operand -> (operand) * ThreadLocalRandom.current().nextInt(99)).average().orElse(999.999)) % 100);
            }
        }
        return FixedMessage.build("**" + context.getAuthor().getEffectiveName() + "** rolls " + ThreadLocalRandom.current().nextInt(100));
    }
}