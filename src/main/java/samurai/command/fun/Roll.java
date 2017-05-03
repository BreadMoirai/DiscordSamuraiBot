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
package samurai.command.fun;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;
import samurai.messages.impl.RollPoll;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Key({"roll", "rollpoll"})
public class Roll extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getKey().equalsIgnoreCase("rollpoll")) {
            final List<String> args = context.getArgs();
            if (!args.isEmpty() && CommandContext.isNumber(args.get(0))) {
                int time = Integer.parseInt(args.get(0));
                if (args.size() == 2) {
                    switch (args.get(1).toLowerCase()) {
                        case "m":
                        case "min":
                        case "minute":
                        case "minutes":
                            return new RollPoll(time, TimeUnit.MINUTES);
                        case "s":
                        case "second":
                        case "seconds":
                            return new RollPoll(time, TimeUnit.SECONDS);
                        case "h":
                        case "hour":
                        case "hours":
                            return new RollPoll(time, TimeUnit.HOURS);
                        case "d":
                        case "day":
                        case "days":
                            return new RollPoll(time, TimeUnit.DAYS);
                        default:break;
                    }
                }
                return new RollPoll(time, TimeUnit.MINUTES);
            }
            return new RollPoll();
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
