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
package com.github.breadmoirai.samurai.command.debug;

import com.github.breadmoirai.samurai.Bot;
import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;

import java.time.Instant;

@Key("uptime")
public class Uptime extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        long timeDifference = Instant.now().getEpochSecond() - Bot.info().START_TIME;
        int seconds = (int) ((timeDifference) % 60);
        int minutes = (int) ((timeDifference / 60) % 60);
        int hours = (int) ((timeDifference / 3600) % 24);
        int days = (int) (timeDifference / 86400);
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(String.format("%d days, ", days));
        if (hours > 0) sb.append(String.format("%d hours, ", hours));
        if (minutes > 0) sb.append(String.format("%d minutes, ", minutes));
        sb.append(String.format("%d seconds.", seconds));
        return FixedMessage.build(sb.toString());
    }
}

