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
package com.github.breadmoirai.discord.bot.modules.prefix;

import com.github.breadmoirai.breadbot.framework.builder.BreadBotClientBuilder;
import com.github.breadmoirai.breadbot.modules.prefix.PrefixCommand;
import com.github.breadmoirai.breadbot.plugins.hocon.HOCONConfigurable;
import com.github.breadmoirai.breadbot.plugins.prefix.PrefixPlugin;
import com.github.breadmoirai.database.Database;
import com.typesafe.config.Config;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONException;

import java.util.Map;

public class DynamicPrefixModule implements PrefixPlugin, HOCONConfigurable {

    private final String defaultPrefix;

    public DynamicPrefixModule(String prefix) {
        this.defaultPrefix = prefix;
    }

    @Override
    public void initialize(BreadBotClientBuilder client) {
        if (!Database.hasTable("GuildPrefix")) {
            Database.get().useHandle(handle -> handle.execute("CREATE TABLE GuildPrefix ( " +
                    "guild BIGINT PRIMARY KEY, " +
                    "prefix VARCHAR(20))"));
        }
        client.addCommand(PrefixCommand.class);
    }

    @Override
    public String getPrefix(Guild guild) {
        final long guildId = guild.getIdLong();
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
    public void buildConfig(Guild guild, Map<String, Object> conf) {
        conf.put("prefix", getPrefix(guild));
    }

    @Override
    public boolean loadConfig(Guild guild, Config conf) {
        if (!conf.hasPath("prefix")) return false;
        try {
            final String prefix = conf.getString("prefix");
            if (prefix.isEmpty()) return false;
            if (!prefix.equals(getPrefix(guild))) {
                changePrefix(guild.getIdLong(), prefix);
            }
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}