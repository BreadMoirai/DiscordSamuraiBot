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
package com.github.breadmoirai.samurai.plugins.derby.points;

import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.derby.MissingDerbyPluginException;
import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DerbyPointPlugin extends ListenerAdapter implements CommandPlugin {

    private static final double MESSAGE_POINT = 2;
    private static final double MINUTE_POINT = .09;
    private static final double VOICE_POINT = 3;

    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
    private TLongObjectMap<PointSession> pointMap;
    private HashSet<VoiceChannel> voiceChannels;
    private PointExtension database;


    @Override
    public void initialize(BreadBotBuilder builder) {
        if (!builder.hasPlugin(DerbyDatabase.class)) {
            throw new MissingDerbyPluginException();
        }
        final DerbyDatabase database = builder.getPlugin(DerbyDatabase.class);
        this.database = database.getExtension(PointExtension::new);
    }

    @Override
    public void onReady(ReadyEvent event) {
        voiceChannels = new HashSet<>(20);
        final List<Guild> guilds = event.getJDA().getGuilds();
        pointMap = TCollections.synchronizedMap(new TLongObjectHashMap<>(guilds.size() + 5));

        for (Guild guild : guilds) {
            for (Member member : guild.getMembers()) {

                final OnlineStatus onlineStatus = member.getOnlineStatus();
                if (onlineStatus == OnlineStatus.ONLINE || onlineStatus == OnlineStatus.IDLE) {

                    final long idLong = member.getUser().getIdLong();
                    if (!pointMap.containsKey(idLong)) {
                        pointMap.put(idLong, database.getPointSession(idLong, onlineStatus));
                    }
                }
            }
        }
        pool.scheduleAtFixedRate(this::addMinutePoints, 2, 1, TimeUnit.MINUTES);
        pool.scheduleAtFixedRate(this::addVoicePoints, 2, 1, TimeUnit.MINUTES);
    }

    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        if (event.getUser().isBot() || event.getUser().isFake()) return;
        final User user = event.getUser();
        long userId = user.getIdLong();
        final OnlineStatus onlineStatus = event.getGuild().getMember(user).getOnlineStatus();
//        final OnlineStatus previousOnlineStatus = event.getPreviousOnlineStatus();
        switch (onlineStatus) {
            case INVISIBLE:
            case OFFLINE:
            case DO_NOT_DISTURB:
            case UNKNOWN: {
                PointSession points = pointMap.get(userId);
                if (points != null) {
                    pointMap.remove(userId);
                    points.commit();
                }
            }
            break;

            case IDLE:
            case ONLINE: {
                PointSession pointSession = pointMap.get(userId);
                if (pointSession != null) {
                    pointSession.setStatus(onlineStatus);
                } else {
                    final PointSession sesh = database.getPointSession(userId, onlineStatus);
                    sesh.setStatus(onlineStatus);
                    pointMap.put(userId, sesh);
                }
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        PointSession pointSession = pointMap.get(event.getAuthor().getIdLong());
        if (pointSession != null) {
            final long now = event.getMessage().getCreationTime().toInstant().getEpochSecond();
            final long diff = now - pointSession.getLastMessageSent();
            if (diff < 5 || diff > 95) {
                pointSession.offsetPoints(MESSAGE_POINT);
            } else {
                final double U = diff / 90;
                final int sigma = 2;
                final double X = sigma * Math.sqrt(-2 * Math.log(U));
                final double r = X * 10 + MESSAGE_POINT;
                pointSession.offsetPoints(ThreadLocalRandom.current().nextDouble(r));
            }
            pointSession.setLastMessageSent(now);
        }

    }

    private void addMinutePoints() {
        pointMap.valueCollection().parallelStream().forEach(pointSession -> pointSession.offsetPoints(MINUTE_POINT));
    }


    public void offsetPoints(long userId, double pointValue) {
        PointSession pointSession = pointMap.get(userId);
        if (pointSession != null) {
            pointSession.offsetPoints(pointValue);
        } else {
            final PointSession session = database.getPointSession(userId, null);
            session.offsetPoints(pointValue);
            session.commit();
        }
    }

    private void offsetPoints(Member member, double pointValue) {
        offsetPoints(member.getUser().getIdLong(), pointValue);
    }

    public void transferPoints(long from, long to, double amount) {
        offsetPoints(from, -1 * amount);
        offsetPoints(to, amount);
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        pointMap.valueCollection().parallelStream().forEach(PointSession::commit);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        voiceChannels.remove(event.getChannelLeft());
        if (event.getChannelLeft().getMembers().size() > 0)
            voiceChannels.add(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        voiceChannels.remove(event.getChannelJoined());
        voiceChannels.add(event.getChannelJoined());
    }

    private void addVoicePoints() {
        for (VoiceChannel voiceChannel : voiceChannels) {
            final List<Member> members = voiceChannel.getMembers();
            if (members.stream().filter(member -> !member.getUser().isBot()).count() >= 2)
                for (Member member : members) {
                    if (!member.getVoiceState().isMuted() && !member.getUser().isBot())
                        offsetPoints(member, VOICE_POINT);
                }
        }
    }

    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        voiceChannels.remove(event.getVoiceState().getChannel());
        voiceChannels.add(event.getVoiceState().getChannel());
    }

}
