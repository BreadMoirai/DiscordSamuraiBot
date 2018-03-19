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
import org.jdbi.v3.core.statement.Update;

import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collector;

public class RollPollExtension extends JdbiExtension {

    public RollPollExtension(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("DailyRollPollChannels")) {
            execute("CREATE TABLE DailyRollPollChannels (\n" +
                    "  GuildId       BIGINT NOT NULL PRIMARY KEY,\n" +
                    "  ChannelId     BIGINT NOT NULL\n" +
                    ")");
        }
        if (tableAbsent("DailyRollPollStorage")) {
            execute("CREATE TABLE DailyRollPollStorage (\n" +
                    "  GuildId      BIGINT NOT NULL,\n" +
                    "  MemberId     BIGINT NOT NULL,\n" +
                    "  Roll         INT NOT NULL,\n" +
                    "  CONSTRAINT   DRPS_PK PRIMARY KEY (GuildId, MemberId)\n" +
                    ")");
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

    public void storeRolls(long guildId, List<RollPollMessage.Roll> rolls) {
        execute("DELETE FROM DailyRollPollStorage WHERE GuildId = ?", guildId);
        useHandle(handle -> {
            final Update update = handle.createUpdate("INSERT INTO DailyRollPollStorage VALUES (?, ?, ?)");
            update.bind(0, guildId);
            for (RollPollMessage.Roll roll : rolls) {
                update.bind(1, roll.getMemberId());
                update.bind(2, roll.getRoll());
                update.execute();
            }
        });
    }

    public List<RollPollMessage.Roll> getStoredRolls(long guildId) {
        return withHandle(handle -> handle.select("SELECT MemberId, Roll FROM DailyRollPollStorage WHERE GuildId = ?", guildId)
                .map((r, ctx) -> new RollPollMessage.Roll(r.getLong(1), r.getInt(2)))
                .list());
    }

}
