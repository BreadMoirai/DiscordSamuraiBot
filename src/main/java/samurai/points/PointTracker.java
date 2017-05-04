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
package samurai.points;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import samurai.command.CommandModule;
import samurai.database.Database;
import samurai.database.dao.GuildDao;
import samurai.database.objects.SamuraiGuild;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class PointTracker {
    HashMap<Long, HashMap<Long, PointSession>> pointSessions;

    public void load(ReadyEvent event) {
        final List<Guild> guilds = event.getJDA().getGuilds();
        pointSessions = new HashMap<>(guilds.size());
        for (Guild guild : guilds) {
            final long guildId = guild.getIdLong();
            Function<GuildDao, SamuraiGuild> guildGet = guildDao -> guildDao.getGuild(guildId);
            final SamuraiGuild samuraiGuild = Database.get().openDao(GuildDao.class, guildGet);
            if (CommandModule.points.isEnabled(samuraiGuild.getModules())) {
                final HashMap<Long, PointSession> sessions = new HashMap<>(guild.getMembers().size());
                pointSessions.put(guildId, sessions);
                for (Member member : guild.getMembers()) {
                    final OnlineStatus onlineStatus = member.getOnlineStatus();
                    if (onlineStatus != OnlineStatus.UNKNOWN && onlineStatus != OnlineStatus.OFFLINE) {
                        final long memberId = member.getUser().getIdLong();
                        sessions.put(memberId, Database.get().getPointSession(guildId, memberId).setStatus(onlineStatus));
                    }
                }
            }
        }
    }

    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        System.out.println(event.getPreviousOnlineStatus());
        System.out.println(event.getGuild().getMember(event.getUser()).getOnlineStatus());
        final OnlineStatus previousOnlineStatus = event.getPreviousOnlineStatus();
        switch(event.getGuild().getMember(event.getUser()).getOnlineStatus()) {
            case OFFLINE:
            case UNKNOWN:
                    pointSessions.values().stream().map(longPointSessionHashMap -> longPointSessionHashMap.remove(event.getUser().getIdLong())).forEach(PointSession::destroy);
                    break;
                    default:
        }
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

    }
}
