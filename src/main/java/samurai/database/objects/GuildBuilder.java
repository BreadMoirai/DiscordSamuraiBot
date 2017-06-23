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
package samurai.database.objects;

import org.apache.commons.lang3.tuple.Pair;
import samurai.database.Database;
import samurai.database.dao.GuildDao;

import java.util.List;

public class GuildBuilder {
    private long guildId;
    private String prefix;
    private List<Player> players = null;
    private List<Chart> charts = null;
    private List<Pair<Long, Short>> channelModes = null;
    private long modules;

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    public void setChannelModes(List<Pair<Long, Short>> channelModes) {
        this.channelModes = channelModes;
    }

    public void setModules(long modules) {
        this.modules = modules;
    }

    public GuildBuilder putGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public GuildBuilder putPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GuildBuilder putPlayers(List<Player> players) {
        this.players = players;
        return this;
    }

    public GuildBuilder putCharts(List<Chart> charts) {
        this.charts = charts;
        return this;
    }

    public GuildBuilder putChannelModes(List<Pair<Long, Short>> channelModes) {
        this.channelModes = channelModes;
        return this;
    }

    public GuildBuilder putModules(long modules) {
        this.modules = modules;
        return this;
    }

    public SamuraiGuild build() {
        return new SamuraiGuild(guildId, prefix, players, charts, channelModes, modules);
    }

    public SamuraiGuild create() {
        final SamuraiGuild build = build();
        Database.get().<GuildDao>openDao(GuildDao.class, guildDao -> guildDao.insertGuild(build));
        return build;
    }

    public long getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Chart> getCharts() {
        return charts;
    }

    public List<Pair<Long, Short>> getChannelModes() {
        return channelModes;
    }

    public long getModules() {
        return modules;
    }
}