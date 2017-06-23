/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.database.dao;

import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import samurai.database.mapper.ChannelModeMapper;

public interface ChannelDao {
    @SqlUpdate("INSERT INTO ChannelMode VALUES (?, ?, ?)")
    void insertChannelMode(long guildId, long channelId, short mode);

    @SqlQuery("SELECT COUNT(1) FROM ChannelMode WHERE ChannelID = ?")
    int countChannelMode(long channelId);

    @SqlQuery("SELECT ChannelID, Mode FROM ChannelMode WHERE ChannelID = ?")
    @RegisterRowMapper(ChannelModeMapper.class)
    Pair<Long, Short> getChannelMode(long channelId);

    default boolean hasChannel(long channelId) {
        return countChannelMode(channelId) == 1;
    }

    @SqlUpdate("UPDATE ChannelMode SET Mode = :mode WHERE ChannelID = :channelId")
    void updateChannelMode(@Bind("channelId") long channelId, @Bind("mode") short mode);

    @SqlUpdate("DELETE FROM ChannelMode WHERE ChannelID = ?")
    void deleteChannelMode(long channelId);
}
