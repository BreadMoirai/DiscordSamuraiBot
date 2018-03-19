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

package com.github.breadmoirai.samurai.plugins.personal;

import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import org.jdbi.v3.core.Jdbi;

public class MemberControlPanelDatabase extends JdbiExtension {

    public MemberControlPanelDatabase(Jdbi jdbi) {
        super(jdbi);
        initializeTables();
    }

    private void initializeTables() {
        if (tableAbsent("MemberControlPanels")) {
            execute("CREATE TABLE MemberControlPanels (\n" +
                            "  GuildId       BIGINT NOT NULL PRIMARY KEY,\n" +
                            "  ChannelId     BIGINT NOT NULL,\n" +
                            "  MessageId     BIGINT NOT NULL\n" +
                            ")");
            execute("CREATE TABLE MemberControlPanelOptions (\n" +
                            "  GuildId       BIGINT PRIMARY KEY " +
                            "                CONSTRAINT GuildId_FK REFERENCES MemberControlPanels(GuildId),\n" +
                            "  Emoji         VARCHAR(12) NOT NULL,\n" +
                            "  ChannelId     BIGINT NOT NULL\n" +
                            ")");
        }
    }
}
