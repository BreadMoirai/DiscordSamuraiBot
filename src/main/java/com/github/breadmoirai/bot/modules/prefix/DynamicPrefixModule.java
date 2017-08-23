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
 */
package com.github.breadmoirai.bot.modules.prefix;

import com.github.breadmoirai.database.Database;
import com.github.breadmoirai.framework.core.SamuraiClient;
import com.github.breadmoirai.framework.core.impl.CommandEngineBuilder;
import com.github.breadmoirai.framework.modules.prefix.IPrefixModule;
import com.github.breadmoirai.framework.modules.prefix.PrefixCommand;
import org.json.JSONException;
import org.json.JSONObject;

public class DynamicPrefixModule implements IPrefixModule {

    private final String defaultPrefix;

    public DynamicPrefixModule(String prefix) {
        this.defaultPrefix = prefix;
    }

    @Override
    public void init(CommandEngineBuilder engineBuilder, SamuraiClient client) {
        if (!Database.hasTable("GuildPrefix")) {
            Database.get().useHandle(handle -> handle.execute("CREATE TABLE GuildPrefix ( " +
                    "guild BIGINT PRIMARY KEY, " +
                    "prefix VARCHAR(20))"));
        }
        engineBuilder.registerCommand(PrefixCommand.class);
    }

    @Override
    public String getPrefix(long guildId) {
        return Database.get().withHandle(handle ->
                handle.select("SELECT prefix FROM GuildPrefix WHERE guild = ?", guildId)
                        .mapTo(String.class)
                        .findFirst()
                        .orElseGet(() -> {
                            handle.insert("INSERT INTO GuildPrefix VALUES (?, ?)", guildId, defaultPrefix);
                            return defaultPrefix;
                        }));
    }


    public void changePrefix(long guildId, String newPrefix) {
        Database.get().useHandle(handle -> handle.update("UPDATE GuildPrefix SET prefix = ? WHERE guild = ?", newPrefix, guildId));
    }

    @Override
    public boolean isJSONconfigurable() {
        return true;
    }

    @Override
    public void addJSONconfig(long guildId, JSONObject jsonObject) {
        jsonObject.put("prefix", getPrefix(guildId));
    }

    @Override
    public boolean loadJSONconfig(long guildId, JSONObject jsonObject) {
        if (!jsonObject.has("prefix")) return false;
        try {
            final String prefix = jsonObject.getString("prefix");
            if (prefix.isEmpty()) return false;
            if (!prefix.equals(getPrefix(guildId))) {
                changePrefix(guildId, prefix);
            }
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

}