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
package com.github.breadmoirai.discord.bot.modules.points;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PointDao {

    //    CREATE TABLE MemberPoints (
    //        UserId  BIGINT NOT NULL,
    //        GuildId BIGINT NOT NULL,
    //        Points  DOUBLE DEFAULT 0,
    //        Exp     DOUBLE DEFAULT 0,
    //        CONSTRAINT Points_PK PRIMARY KEY (DiscordId, GuildId)
    //    );


    @SqlUpdate("INSERT INTO MemberPoints(UserId, GuildId) VALUES (:uid, :gid)")
    void insertUser(@Bind("uid") long discordId, @Bind("gid") long guildId);

    @SqlUpdate("DELETE FROM MemberPoints WHERE UserId = :uid AND GuildId = :gid")
    void deleteUser(@Bind("uid") long userId, @Bind("gid") long guildId);

    @SqlUpdate("UPDATE MemberPoints SET Points = :pts, Exp = :exp WHERE UserId = :uid AND GuildId = :gid")
    void update(@Bind("uid") long userId, @Bind("gid") long guildId, @Bind("pts") double points, @Bind("exp") double exp);

    @SqlQuery("SELECT * FROM MemberPoints WHERE UserId = :uid AND GuildId = :gid")
    @RegisterBeanMapper(PointSession.class)
    PointSession getSession(@Bind("uid") long userId, @Bind("gid") long guildId);

}
