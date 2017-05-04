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
import samurai.database.objects.SamuraiGuild;
import samurai.database.objects.GuildBuilder;
import samurai.database.objects.Player;
import samurai.database.objects.PlayerBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface GuildDao {
    @SqlUpdate("INSERT INTO Guild VALUES (:guildId, :prefix, :modules)")
    void insertGuild(@BindBean SamuraiGuild guild);

    @SqlUpdate("INSERT INTO GuildChart VALUES (?, ?)")
    void insertChartAssociation(long guildId, int chartId);

    @SqlUpdate("INSERT INTO GuildPlayer VALUES (:id1, :id2, :mode)")
    void insertPlayerAssociation(@Bind("id1") long guildId, @Bind("id2") long userId, @Bind("mode") short mode);

    @SqlQuery("SELECT Player.* FROM GuildPlayer JOIN Player ON GuildPlayer.DiscordId = Player.DiscordId WHERE GuildId = :id")
    @RegisterBeanMapper(PlayerBuilder.class)
    List<PlayerBuilder> selectPlayers(@Bind("id") long guildId);

    default List<Player> getPlayers(long guildId) {
        return selectPlayers(guildId).stream().map(PlayerBuilder::build).collect(Collectors.toCollection(ArrayList<Player>::new));
    }

    @SqlQuery("SELECT ChannelID, Mode FROM ChannelMode WHERE GuildId = :id")
    @RegisterRowMapper(ChannelModeMapper.class)
    List<Pair<Long, Short>> getChannelModes(@Bind("id") long guildId);

    @SqlQuery("SELECT * FROM Guild WHERE GuildID = :id")
    @RegisterBeanMapper(GuildBuilder.class)
    GuildBuilder selectGuild(@Bind("id") long guildId);

    @SqlQuery("SELECT Guild.Prefix FROM Guild WHERE GuildId = :id")
    String getPrefix(@Bind("id") long guildId);

    default SamuraiGuild getGuild(long guildId) {
        GuildBuilder guildBuilder = selectGuild(guildId);
        if (guildBuilder == null) return null;
        else return guildBuilder.build();
    }

    @SqlUpdate("UPDATE Guild SET Prefix = :prefix WHERE GuildId = :guildId")
    void updatePrefix(@Bind("guildId") long guildId, @Bind("prefix") String prefix);

    @SqlUpdate("UPDATE Guild SET Modules = :modules WHERE GuildId = :guildId")
    void updateModules(@Bind("guildId") long guildId, @Bind("modules") long modules);

    @SqlUpdate("DELETE FROM GuildChart WHERE GuildId = :id")
    void deleteGuildCharts(@Bind("id") long guildId);

    @SqlUpdate("DELETE FROM GuildPlayer WHERE GuildId = :id")
    void deleteGuildPlayers(@Bind("id") long guildId);

    @SqlUpdate("DELETE FROM Guild WHERE GuildId = :id")
    void deleteGuild(@Bind("id") long guildId);

    default void destroyGuild(long guildId) {
        deleteGuildCharts(guildId);
        deleteGuildPlayers(guildId);
        deleteGuild(guildId);
    }

    @SqlQuery("SELECT Guild.GuildId FROM Guild")
    List<Long> getGuilds();
}
