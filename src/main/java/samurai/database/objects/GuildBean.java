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
import samurai.database.dao.ChartDao;
import samurai.database.dao.GuildDao;
import samurai.database.dao.PlayerDao;
import samurai.osu.enums.GameMode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * manages primary specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class GuildBean {
    private long guildId;
    private String prefix;
    private List<PlayerBean> players;
    private List<ChartBean> charts;
    private List<Pair<Long, Short>> channelModes;
    private long modules;

    GuildBean(long guildId, String prefix, List<PlayerBean> players, List<ChartBean> charts, List<Pair<Long, Short>> channelModes, long modules) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.players = players;
        this.charts = charts;
        this.channelModes = channelModes;
        this.modules = modules;
    }

    public long getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<PlayerBean> getPlayers() {
        if (players == null) {
            players = Database.get().<GuildDao, List<PlayerBean>>openDao(GuildDao.class, guildDao -> guildDao.getPlayers(getGuildId()));
        }
        return Collections.unmodifiableList(players);
    }

    public List<ChartBean> getCharts() {
        if (charts == null) {
            charts = Database.get().<ChartDao, List<ChartBean>>openDao(ChartDao.class, chartDao -> chartDao.getGuildCharts(getGuildId()));
        }
        return Collections.unmodifiableList(charts);
    }

    public List<Pair<Long, Short>> getChannelModes() {
        if (channelModes == null) {
            channelModes = Database.get().<GuildDao, List<Pair<Long, Short>>>openDao(GuildDao.class, guildDao -> guildDao.getChannelModes(getGuildId()));
        }
        return Collections.unmodifiableList(channelModes);
    }

    public long getModules() {
        return modules;
    }

    public Optional<PlayerBean> getPlayer(long discordUserId) {
        if (players == null) {
            return Optional.ofNullable(Database.get().<PlayerDao, PlayerBean>openDao(PlayerDao.class, playerDao -> playerDao.getPlayer(discordUserId)));
        } else return players.stream().filter(playerBean -> playerBean.getDiscordId() == discordUserId).findAny();
    }

    public int getRankLocal(PlayerBean player) {
        return players.indexOf(player);
    }

    public GuildUpdater getUpdater() {
        return new GuildUpdater(guildId);
    }
}
