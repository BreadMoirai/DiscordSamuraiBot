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
package samurai.database.dao;

import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import samurai.database.mapper.ChannelModeMapper;
import samurai.database.objects.GuildBean;
import samurai.database.objects.GuildBuilder;
import samurai.database.objects.PlayerBean;
import samurai.database.objects.PlayerBuilder;
import samurai.osu.enums.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface GuildDao {
    @SqlUpdate("INSERT INTO Guild VALUES (:guildId, :prefix, :modules)")
    void insertGuild(@BindBean GuildBean guild);

    @SqlUpdate("INSERT INTO GuildChart VALUES (?, ?)")
    void insertChartAssociation(long guildId, int chartId);

    @SqlUpdate("INSERT INTO GuildPlayer VALUES (?, ?, ?)")
    void insertPlayerAssociation(long guildId, long userId, byte mode);

    @SqlUpdate("INSERT INTO ChannelMode VALUES (?, ?, ?)")
    void insertChannelMode(long guildId, long channelId, byte mode);

    @SqlUpdate("SELECT Player.* FROM GuildPlayer JOIN Player ON GuildPlayer.UserID = Player.UserID WHERE GuildID = ?")
    @RegisterBeanMapper(PlayerBuilder.class)
    List<PlayerBuilder> selectPlayers(long guildId);

    default List<PlayerBean> getPlayers(long guildId) {
        return selectPlayers(guildId).stream().map(PlayerBuilder::build).collect(Collectors.toCollection(ArrayList<PlayerBean>::new));
    }

    @SqlQuery("SELECT ChannelID, Mode FROM ChannelMode WHERE GuildID = ?")
    @RegisterRowMapper(ChannelModeMapper.class)
    List<Pair<Long, GameMode>> getChannelModes(long guildId);

    @SqlQuery("SELECT ChannelID, Mode FROM ChannelMode WHERE GuildID = ? AND ChannelID = ?")
    @RegisterRowMapper(ChannelModeMapper.class)
    Pair<Long, GameMode> getChannelMode(long guildId, long channelId);

    @SqlQuery("SELECT * FROM Guild WHERE GuildID = ?")
    @RegisterBeanMapper(GuildBuilder.class)
    GuildBuilder selectGuild(long guildId);

}
