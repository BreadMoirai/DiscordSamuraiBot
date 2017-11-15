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
package com.github.breadmoirai.bot.modules.points;

import com.github.breadmoirai.bot.framework.BreadBotClient;
import com.github.breadmoirai.bot.framework.CommandEngineBuilder;
import com.github.breadmoirai.bot.framework.ICommandModule;
import com.github.breadmoirai.database.Database;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PointModule extends ListenerAdapter implements ICommandModule {

    private static final double MINIMUM = 1, LOWER_LIMIT = 4, UPPER_LIMIT = 15;
    private static final double MINUTE_POINT = .007;
    private static final double VOICE_POINT = 10;

    private static final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, PointSession>> guildPointMap;
    private HashSet<VoiceChannel> voiceChannels;

    @Override
    public String getName() {
        return "points";
    }

    @Override
    public void init(CommandEngineBuilder ceb, BreadBotClient samuraiClient) {
        ceb.registerCommand(this.getClass().getPackage().getName() + ".command");
        if (!Database.hasTable("MemberPoints")) {
            Database.get().useHandle(handle -> handle
                    .execute("CREATE TABLE MemberPoints (\n" +
                            "  UserId  BIGINT NOT NULL,\n" +
                            "  GuildId BIGINT NOT NULL,\n" +
                            "  Points  DOUBLE DEFAULT 0,\n" +
                            "  Exp     DOUBLE DEFAULT 0,\n" +
                            "  CONSTRAINT Points_PK PRIMARY KEY (UserId, GuildId)\n" +
                            ")"));
        }
    }

    @Override
    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        voiceChannels = new HashSet<>(20);
        final List<Guild> guilds = event.getJDA().getGuilds();
        guildPointMap = new ConcurrentHashMap<>(guilds.size());
        for (Guild guild : guilds) {
            enablePoints(guild);
        }
        pool.scheduleAtFixedRate(this::addOnlinePoints, 2, 1, TimeUnit.MINUTES);
        pool.scheduleAtFixedRate(this::addVoicePoints, 2, 1, TimeUnit.MINUTES);
    }

    @Override
    @SubscribeEvent
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        if (event.getUser().isBot() || event.getUser().isFake()) return;
        final User user = event.getUser();
        long userId = user.getIdLong();
        final OnlineStatus onlineStatus = event.getGuild().getMember(user).getOnlineStatus();
        long guildId = event.getGuild().getIdLong();
        switch (onlineStatus) {
            case OFFLINE:
            case UNKNOWN: {
                ConcurrentHashMap<Long, PointSession> guildSession = guildPointMap.get(guildId);
                if (guildSession != null) {
                    PointSession pointSession = guildSession.remove(userId);
                    if (pointSession != null) {
                        pointSession.commit();
                    }
                }
            }
            break;
            default: {
                ConcurrentHashMap<Long, PointSession> guildSession = guildPointMap.get(guildId);
                if (guildSession != null) {
                    guildSession.computeIfAbsent(userId, id -> getDBPointSession(guildId, id));
                }
            }
        }
    }

    @Override
    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        ConcurrentHashMap<Long, PointSession> guildSessions = guildPointMap.get(event.getGuild().getIdLong());
        if (guildSessions != null) {
            PointSession pointSession = guildSessions.get(event.getAuthor().getIdLong());
            if (pointSession != null) {
                final long now = event.getMessage().getCreationTime().toInstant().getEpochSecond();
                final long diff = now - pointSession.getLastMessageSent();
                if (diff > 900) {
                    pointSession.offsetPoints(UPPER_LIMIT);
                    pointSession.offsetExp(UPPER_LIMIT);
                } else if (diff < 5) {
                    pointSession.offsetPoints(MINIMUM);
                    pointSession.offsetExp(MINIMUM);
                } else if (diff < 10) {
                    pointSession.offsetPoints(LOWER_LIMIT);
                    pointSession.offsetExp(LOWER_LIMIT);
                } else {
                    double offset = 625.0 * Math.pow(Math.atan(diff / 15.0), 3.0) / (Math.PI * Math.sqrt(diff)) - 10.0;
                    pointSession.offsetPoints(offset);
                    pointSession.offsetExp(offset);
                }
                pointSession.setLastMessageSent(now);
            }
        }
    }

    private void addOnlinePoints() {
        guildPointMap.values().parallelStream().map(ConcurrentHashMap::values).flatMap(Collection::stream).forEach(pointSession -> pointSession.offsetPoints(MINUTE_POINT));
    }

    public PointSession getPointSession(long guildId, long userId) {
        ConcurrentHashMap<Long, PointSession> guildSession = guildPointMap.get(guildId);
        if (guildSession != null) {
            PointSession pointSession = guildSession.get(userId);
            if (pointSession != null) {
                return pointSession;
            }
        }
        return getDBPointSession(guildId, userId);
    }

    private PointSession getDBPointSession(long guildId, long userId) {
        return Database.get().withExtension(PointDao.class, pointDao -> {
            final PointSession session = pointDao.getSession(userId, guildId);
            if (session == null) {
                pointDao.insertUser(userId, guildId);
                final PointSession pointSession = new PointSession();
                pointSession.setGuildId(guildId);
                pointSession.setUserId(userId);
                pointSession.setPoints(0);
                return pointSession;
            } else return session;
        });
    }


    public PointSession getPointSession(Member member) {
        return getPointSession(member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public void offsetPoints(long guildId, long userId, double pointValue) {
        ConcurrentHashMap<Long, PointSession> guildSession = guildPointMap.get(guildId);
        if (guildSession != null) {
            PointSession pointSession = guildSession.get(userId);
            if (pointSession != null) {
                pointSession.offsetPoints(pointValue);
            } else {
                getDBPointSession(guildId, userId).offsetPoints(pointValue).commit();
            }
        }
    }

    public void offsetPoints(Member member, double pointValue) {
        offsetPoints(member.getGuild().getIdLong(), member.getUser().getIdLong(), pointValue);
    }

    public double transferPoints(long guildId, long fromUserId, long toUserId, double ratio) {
        double transfer = (getPointSession(guildId, fromUserId).getPoints() * ratio);
        offsetPoints(guildId, fromUserId, -1 * transfer);
        offsetPoints(guildId, toUserId, transfer);
        return transfer;
    }

    @Override
    @SubscribeEvent
    public void onShutdown(ShutdownEvent event) {
        pool.shutdown();
        guildPointMap.forEachValue(100L, guildPoints -> guildPoints.forEachValue(100L, PointSession::commit));
    }

    @Override
    @SubscribeEvent
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        voiceChannels.remove(event.getChannelLeft());
        if (event.getChannelLeft().getMembers().size() > 0)
            voiceChannels.add(event.getChannelLeft());
    }

    @Override
    @SubscribeEvent
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        voiceChannels.remove(event.getChannelJoined());
        voiceChannels.add(event.getChannelJoined());
    }

    private void addVoicePoints() {
        for (VoiceChannel voiceChannel : voiceChannels) {
            final List<Member> members = voiceChannel.getMembers();
            if (members.stream().filter(member -> !member.getUser().isBot()).count() >= 2)
                for (Member member : members) {
                    if (!member.getVoiceState().isDeafened() && !member.getVoiceState().isMuted() && !member.getUser().isBot())
                        offsetPoints(member, VOICE_POINT);
                }
        }
    }

    @Override
    @SubscribeEvent
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        voiceChannels.remove(event.getVoiceState().getChannel());
        voiceChannels.add(event.getVoiceState().getChannel());
    }

    @Override
    @SubscribeEvent
    public void onGuildLeave(GuildLeaveEvent event) {
        final ConcurrentHashMap<Long, PointSession> guildPoints = guildPointMap.remove(event.getGuild().getIdLong());
        if (guildPoints != null) {
            guildPoints.forEachEntry(1000L, memberPoints -> memberPoints.getValue().commit());
        }
    }

    public void enablePoints(Guild guild) {
        final long guildId = guild.getIdLong();
        if (guildPointMap.containsKey(guildId)) return;
        final List<Member> members = guild.getMembers();
        final ConcurrentHashMap<Long, PointSession> membersPoints = new ConcurrentHashMap<>(members.size());
        guildPointMap.put(guildId, membersPoints);
        for (Member member : members) {
            if ((member.getUser().isBot() || member.getUser().isFake())
                    && member.getUser().getIdLong() != guild.getJDA().getSelfUser().getIdLong())
                continue;
            final OnlineStatus onlineStatus = member.getOnlineStatus();
            if (onlineStatus != OnlineStatus.UNKNOWN && onlineStatus != OnlineStatus.OFFLINE) {
                final long memberId = member.getUser().getIdLong();
                membersPoints.put(memberId, getDBPointSession(guildId, memberId));
            }
        }
    }

    public double getPoints(Member member) {
        return getPointSession(member).getPoints();
    }
    public double getPoints(long guildId, long userId) {
        return getPointSession(guildId, userId).getPoints();
    }
}