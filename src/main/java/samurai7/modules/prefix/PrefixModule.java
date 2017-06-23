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
package samurai7.modules.prefix;

import samurai7.core.Database;
import samurai7.core.IModule;
import samurai7.core.CommandProcessorConfiguration;

public class PrefixModule implements IModule {

    private final String defaultPrefix;

    public PrefixModule(String prefix) {
        this.defaultPrefix = prefix;
    }

    @Override
    public void init(CommandProcessorConfiguration config) {
        if (!Database.get().tableExists("prefix")) {
            Database.get().useHandle(handle -> handle.execute("CREATE TABLE GuildPrefix ( " +
                    "guild BIGINT PRIMARY KEY, " +
                    "prefix VARCHAR(20))"));
        }
        config.addPostProcessPredicate(command -> command.getEvent().validate(getPrefix(command.getEvent().getGuildId())));
        config.registerCommand("samurai7.modules.prefix");
    }

    private String getPrefix(long guildId) {
        return Database.get().withHandle(handle ->
                handle.select("SELECT prefix FROM GuildPrefix WHERE guild = ?", guildId)
                        .mapTo(String.class)
                        .findFirst()
                        .orElseGet(() -> {
                            handle.insert("INSERT INTO GuildPrefix VALUES (?, ?)", guildId, defaultPrefix);
                            return defaultPrefix;
                        }));
    }

    void changePrefix(long guildId, String newPrefix) {
        Database.get().useHandle(handle -> handle.update("UPDATE Guild SET Prefix = ? WHERE GuildId = ?", newPrefix, guildId));
    }

}
