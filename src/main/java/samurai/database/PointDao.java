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
package com.github.breadmoirai.samurai.database.dao;

import com.github.breadmoirai.samurai.points.PointSession;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PointDao {

    @SqlUpdate("INSERT INTO MemberPoints(DiscordId, GuildId) VALUES (:id, :guildId)")
    void insertUser(@Bind("id") long discordId, @Bind("guildId") long guildId);

    @SqlUpdate("DELETE FROM MemberPoints WHERE DiscordId = :id1 AND GuildId = :id2")
    void deleteUser(@Bind("id1") long userId, @Bind("id2") long guildId);

    @SqlUpdate("UPDATE MemberPoints SET Points = :p WHERE DiscordId = :d AND GuildId = :g")
    void update(@Bind("d") long userId, @Bind("g") long guildId, @Bind("p") double points);

    @SqlQuery("SELECT * FROM MemberPoints WHERE DiscordId = :d AND GuildId = :g")
    @RegisterBeanMapper(PointSession.class)
    PointSession getSession(@Bind("d") long userId, @Bind("g") long guildId);

}
