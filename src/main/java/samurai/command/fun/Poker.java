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
import net.dv8tion.jda.core.requests.restaction.ChannelAction;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.impl.poker.PokerBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Key("poker")
@Admin
public class Poker extends Command {
    public static final int SEAT_COUNT = 5;

    private static final Permission[] GUILD_PERMISSIONS = {Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES};
    private static final Permission[] CHANNEL_PERMISSIONS = {Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};

    private static final long LOG_PERMISSIONS_ALLOW = Permission.getRaw(Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION);
    private static final long LOG_PERMISSIONS_DENY = Permission.getRaw(Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE);
    private static final long RAW_CHANNEL_PERMISSIONS = Permission.getRaw(CHANNEL_PERMISSIONS);
    private static final long RAW_PUBLIC_PERMISSIONS = Permission.getRaw(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES);

    private static final String[] FACES = new String[]{"\ud83d\udc36", "\ud83d\udc31", "\ud83d\udc2d", "\ud83d\udc39", "\ud83d\udc30", "\ud83d\udc3b", "\ud83d\udc3c", "\ud83d\udc28", "\ud83d\udc2f", "\ud83e\udd81", "\ud83d\udc2e", "\ud83d\udc37", "\ud83d\udc38", "\ud83d\udc19", "\ud83d\udc35", "\ud83d\udc14", "\ud83d\udc27", "\ud83d\udc26", "\ud83d\udc25", "\ud83d\udc3a", "\ud83d\udc17", "\ud83d\udc34", "\ud83e\udd84", "\ud83d\udc1d", "\ud83d\udc1b", "\ud83d\udc0c", "\ud83d\udc1e", "\ud83d\udc1c", "\ud83d\udd77", "\ud83e\udd82", "\ud83e\udd80", "\ud83d\udc0d", "\ud83d\udc22", "\ud83d\udc20", "\ud83d\udc1f", "\ud83d\udc2c", "\ud83d\udc21", "\ud83d\udc33", "\ud83d\udc0b", "\ud83d\udc0a", "\ud83d\udc06", "\ud83d\udc05", "\ud83d\udc03", "\ud83d\udc02", "\ud83d\udc04", "\ud83d\udc2a", "\ud83d\udc2b", "\ud83d\udc18", "\ud83d\udc10", "\ud83d\udc0f", "\ud83d\udc11", "\ud83d\udc0e", "\ud83d\udc16", "\ud83d\udc00", "\ud83d\udc01", "\ud83d\udc13", "\ud83e\udd83", "\ud83d\udd4a", "\ud83d\udc15", "\ud83d\udc29", "\ud83d\udc08", "\ud83d\udc07", "\ud83d\udc3f", "\ud83d\udc32", "\ud83e\udd91", "\ud83e\udd88", "\ud83e\udd85", "\ud83e\udd86", "\ud83e\udd87", "\ud83e\udd89", "\ud83e\udd8a", "\ud83e\udd8b", "\ud83e\udd8c", "\ud83e\udd8d", "\ud83e\udd8e", "\ud83e\udd8f", "\ud83e\udd90", "\ud83e\udd40", "\ud83c\udf39", "\ud83c\udf37", "\ud83c\udf3c"};


    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(GUILD_PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), GUILD_PERMISSIONS);
        }
        final Guild guild = context.getGuild();
        final GuildController controller = guild.getController();
        ChannelAction pokerChannels[] = new ChannelAction[SEAT_COUNT];
        final ChannelAction tableAction = controller
                .createTextChannel("l---Poker_Table---l")
                .setTopic("The Poker Table")
                .addPermissionOverride(guild.getSelfMember(), RAW_CHANNEL_PERMISSIONS, 0)
                .addPermissionOverride(guild.getPublicRole(), RAW_PUBLIC_PERMISSIONS, 0);

        for (int i = 1; i <= SEAT_COUNT; i++) {
            pokerChannels[i-1] = controller
                    .createTextChannel("l---Seat_" + i + "---l")
                    .setTopic(getFace())
                    .addPermissionOverride(guild.getSelfMember(), RAW_CHANNEL_PERMISSIONS, 0)
                    .addPermissionOverride(guild.getPublicRole(), RAW_PUBLIC_PERMISSIONS, 0);
        }
        final ChannelAction logAction = controller
                .createTextChannel("l---Poker__Logs---l")
                .setTopic("A record of wins and losses for the ages")
                .addPermissionOverride(guild.getSelfMember(), RAW_CHANNEL_PERMISSIONS, 0)
                .addPermissionOverride(guild.getPublicRole(), LOG_PERMISSIONS_ALLOW, LOG_PERMISSIONS_DENY);
        return new PokerBuilder(tableAction, pokerChannels, logAction);
    }

    private List<Integer> faces;

    {
        faces = new ArrayList<>();
    }

    private String getFace() {
        int i;
        do {
            i = ThreadLocalRandom.current().nextInt(FACES.length);
        } while (faces.contains(i));
        faces.add(i);
        return FACES[i];
    }

}


