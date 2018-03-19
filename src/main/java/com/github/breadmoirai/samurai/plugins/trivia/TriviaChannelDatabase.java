/*
 *     Copyright 2017-2018 Ton Ly
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
 */

package com.github.breadmoirai.samurai.plugins.trivia;

import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import org.jdbi.v3.core.Jdbi;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.OptionalLong;
import java.util.stream.Collector;

public class TriviaChannelDatabase extends JdbiExtension {

    public TriviaChannelDatabase(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("TriviaChannels")) {
            execute("CREATE TABLE TriviaChannels (\n" +
                            "  GuildId       BIGINT NOT NULL PRIMARY KEY,\n" +
                            "  ChannelId     BIGINT NOT NULL,\n" +
                            "  NextTime      TIMESTAMP\n" +
                            ")");
        }
    }

    public TLongLongMap getTriviaChannels() {
        return withHandle(handle -> {
            return handle.createQuery("SELECT GuildId, ChannelId FROM TriviaChannels")
                         .map((r, ctx) -> new long[]{r.getLong(1), r.getLong(2)})
                         .collect(Collector.<long[], TLongLongMap>of(TLongLongHashMap::new,
                                                                     (map, longs) -> map.put(longs[0], longs[1]),
                                                                     (map1, map2) -> {
                                                                         map1.putAll(map2);
                                                                         return map1;
                                                                     }));
        });
    }

    public void setTriviaChannel(long guildId, long channelId) {
        if (getTriviaChannel(guildId).isPresent()) {
            execute("UPDATE TriviaChannels SET ChannelId = ? WHERE GuildId = ?", channelId, guildId);
        } else {
            execute("INSERT INTO TriviaChannels (GuildId, ChannelId) VALUES (?, ?)", guildId, channelId);
        }
    }

    public OptionalLong getTriviaChannel(long guildId) {
        return selectLong("SELECT ChannelId FROM TriviaChannels WHERE GuildId = ?", guildId);
    }

    public void removeTriviaChannel(long guildId) {
        execute("DELETE FROM TriviaChannels WHERE GuildId = ?", guildId);
    }

    public void setNextTime(long guildId, Instant time) {
        final Timestamp timestamp = Timestamp.valueOf(LocalDateTime.ofInstant(time, ZoneOffset.UTC));
        execute("UPDATE TriviaChannels SET NextTime = ? WHERE GuildId = ? ", timestamp, guildId);
    }

    public Instant getNextTime(long guildId) {
        return selectTimeStamp("SELECT NextTime FROM TriviaChannels WHERE GuildId = ?", guildId)
                .map(Timestamp::toInstant)
                .orElse(Instant.MIN);
    }

}
