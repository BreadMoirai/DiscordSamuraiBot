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

package com.github.breadmoirai.samurai.plugins.rollpoll;

import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collector;

public class RollPollExtension extends JdbiExtension {

    public RollPollExtension(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("DailyRollPollChannels")) {
            execute("CREATE TABLE DailyRollPollChannels (\n" +
                    "  GuildId       BIGINT NOT NULL PRIMARY KEY,\n" +
                    "  ChannelId     BIGINT NOT NULL,\n" +
                    "}");
        }

    }

    public OptionalLong selectRollPollChannel(long guildId) {
        return selectLong("SELECT ChannelId FROM DailyRollPollChannels WHERE GuildId = ?", guildId);
    }

    public void updateRollPollChannel(long guildId, long channelId) {
        execute("UPDATE DailyRollPollChannels SET ChannelId = ? WHERE GuildId = ?", channelId, guildId);
    }


    public void insertRollPollChannel(long guildId, long channelId) {
        execute("INSERT INTO DailyRollPollChannels VALUES (?, ?)", guildId, channelId);
    }

    public void deleteRollPollChannel(long guildId) {
        execute("DELETE FROM DailyRollPollChannels WHERE GuildId = ?", guildId);
    }

    public TLongLongMap getRollPollChannels() {
        return withHandle(handle -> handle
                .select("SELECT * FROM DailyRollPollChannels")
                .map((r, ctx) -> new long[]{r.getLong(1), r.getLong(2)})
                .collect(Collector.<long[], TLongLongMap>of(TLongLongHashMap::new, (map, longs) -> map.put(longs[0], longs[1]), (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                })));
    }

}
