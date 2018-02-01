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
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class RollPollExtension extends JdbiExtension {

    public RollPollExtension(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("RollPollChannels")) {
            execute("CREATE TABLE RollPollChannels (\n" +
                    "  GuildId       BIGINT NOT NULL PRIMARY KEY,\n" +
                    "  ChannelId     BIGINT NOT NULL,\n" +
                    "}");
        }

    }

    public long selectRollPollChannel(long guildId) {
        final Optional<Long> channelId = selectOnly(Long.class, "SELECT ChannelId FROM RollPollChannels WHERE GuildId = ?", guildId);
        return channelId.orElse(0L);
    }

    public void updateRollPollChannel(long guildId, long channelId) {
        execute("UPDATE RollPollChannels SET ChannelId = ? WHERE GuildId ?", channelId, guildId);
    }


    public void insertRollPollChannel(long guildId, long channelId) {
        execute("INSERT INTO RollPollChannels VALUES (?, ?)", guildId, channelId);
    }


}
