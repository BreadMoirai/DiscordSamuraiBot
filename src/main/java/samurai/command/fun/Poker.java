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

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.managers.GuildController;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.impl.poker.PokerBuilder;

import java.util.Arrays;

@Key("poker")
public class Poker extends Command {
    private static final Permission[] GUILD_PERMISSIONS = {Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES};
    private static final Permission[] CHANNEL_PERMISSIONS = {Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};
    private static final int SEAT_COUNT = 1;


    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(GUILD_PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), GUILD_PERMISSIONS);
        }
        final Guild guild = context.getGuild();
        final GuildController controller = guild.getController();
        TextChannel pokerChannels[] = new TextChannel[SEAT_COUNT + 2];
        controller
                .createTextChannel("l---Poker_Table---l")
                .setTopic("The Poker Table")
                .addPermissionOverride(guild.getSelfMember(), Arrays.asList(CHANNEL_PERMISSIONS), null)
                .addPermissionOverride(guild.getPublicRole(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)
                .queue(channel -> {
                    pokerChannels[0] = (TextChannel) channel;
                    for (int i = 1; i <= SEAT_COUNT; i++) {
                        channel.getGuild().getController()
                                .createCopyOfChannel(channel)
                                .setName("l---Seat_" + i + "---l")
                                .setTopic(String.valueOf(i))
                                .queue(channelSeat -> pokerChannels[Integer.parseInt(((TextChannel) channelSeat).getTopic())] = (TextChannel) channelSeat);
                    }
                    channel.getGuild().getController()
                            .createCopyOfChannel(channel)
                            .setName("l---Poker__Logs---l")
                            .setTopic("A record of wins and losses for the ages")
                            .queue(channelEnd -> pokerChannels[SEAT_COUNT + 1] = (TextChannel) channelEnd);
                });
        return new PokerBuilder(pokerChannels);
    }
}

