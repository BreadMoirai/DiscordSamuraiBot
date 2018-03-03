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

package com.github.breadmoirai.samurai.plugins.blacklist;

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import net.dv8tion.jda.core.entities.Member;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class BlackListPlugin implements CommandPlugin {

    @Override
    public void initialize(BreadBotBuilder builder) {

    }

    @Override
    public void onBreadReady(BreadBot client) {
        final DerbyDatabase plugin = client.getPlugin(DerbyDatabase.class);
        final Database database = plugin.getExtension(Database::new);
    }

    @Command
    public void addBlackList(List<Member> members) {

    }

    @Command
    public void removeBlackList(List<Member> members) {

    }

    public boolean isBlacklisted(Member member) {

        return false;
    }

    private class Database extends JdbiExtension {

        public Database(Jdbi jdbi) {
            super(jdbi);
            checkTables();
        }

        private void checkTables() {
            if (tableAbsent("BlackList")) {
                execute("CREATE TABLE BlackList (\n" +
                                "  UserId   BIGINT PRIMARY KEY\n" +
                                ")");
            }
        }
    }
}
