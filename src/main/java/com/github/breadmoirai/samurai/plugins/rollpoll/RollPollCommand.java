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
package com.github.breadmoirai.samurai.plugins.rollpoll;

import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Required;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.samurai.Dispatchable;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.Duration;
import java.time.Instant;

public class RollPollCommand {

    @Command
    public Dispatchable rollpoll(TextChannel channel, @Required Duration duration, EventWaiter waiter) {
        return new RollPollMessage(null, waiter, channel.getGuild().getIdLong(), "RollPoll!", (i, roll) -> "", Instant.now().plus(duration));
    }

    @Command
    public String setupRollPoll(RollPollPlugin plugin, TextChannel channel, Guild guild) {
        final long guildId = guild.getIdLong();
        final long channelId = channel.getIdLong();
        plugin.setDesignatedChannel(guildId, channelId);
        return "Next RollPoll will appear in this channel.";
    }

    @Command
    public String stopRollPoll(RollPollPlugin plugin, Guild guild) {
        plugin.deleteDesignatedChannel(guild.getIdLong());
        return "RollPolls will no longer appear.";
    }

}
