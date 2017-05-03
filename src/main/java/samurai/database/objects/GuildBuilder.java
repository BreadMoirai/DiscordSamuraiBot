/*    Copyright 2017 Ton Ly

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.database.objects;

import org.apache.commons.lang3.tuple.Pair;
import samurai.database.Database;
import samurai.database.dao.GuildDao;
import samurai.osu.enums.GameMode;

import java.util.List;

public class GuildBuilder {
    private long guildId;
    private String prefix;
    private List<PlayerBean> players = null;
    private List<ChartBean> charts = null;
    private List<Pair<Long, Short>> channelModes = null;
    private long modules;

    public GuildBuilder setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public GuildBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GuildBuilder setPlayers(List<PlayerBean> players) {
        this.players = players;
        return this;
    }

    public GuildBuilder setCharts(List<ChartBean> charts) {
        this.charts = charts;
        return this;
    }

    public GuildBuilder setChannelModes(List<Pair<Long, Short>> channelModes) {
        this.channelModes = channelModes;
        return this;
    }

    public GuildBuilder setModules(long modules) {
        this.modules = modules;
        return this;
    }

    public GuildBean build() {
        return new GuildBean(guildId, prefix, players, charts, channelModes, modules);
    }

    public GuildBean create() {
        final GuildBean build = build();
        Database.get().<GuildDao>openDao(GuildDao.class, guildDao -> guildDao.insertGuild(build));
        return build;
    }
}