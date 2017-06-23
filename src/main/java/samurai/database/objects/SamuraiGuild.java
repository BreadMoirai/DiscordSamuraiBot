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
import samurai.database.dao.ChartDao;
import samurai.database.dao.GuildDao;
import samurai.database.dao.PlayerDao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * manages primary specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class SamuraiGuild {
    private long guildId;
    private String prefix;
    private List<Player> players;
    private List<Chart> charts;
    private List<Pair<Long, Short>> channelModes;
    private long modules;

    SamuraiGuild(long guildId, String prefix, List<Player> players, List<Chart> charts, List<Pair<Long, Short>> channelModes, long modules) {
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

    public List<Player> getPlayers() {
        if (players == null) {
            players = Database.get().<GuildDao, List<Player>>openDao(GuildDao.class, guildDao -> guildDao.getPlayers(getGuildId()));
        }
        return Collections.unmodifiableList(players);
    }

    public List<Chart> getCharts() {
        if (charts == null) {
            charts = Database.get().<ChartDao, List<Chart>>openDao(ChartDao.class, chartDao -> chartDao.getGuildCharts(getGuildId()));
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

    public Optional<Player> getPlayer(long discordUserId) {
        return getPlayers().stream().filter(playerBean -> playerBean.getDiscordId() == discordUserId).findAny();
    }

    public int getRankLocal(Player player) {
        return getPlayers().indexOf(player);
    }

    public GuildUpdater getUpdater() {
        return new GuildUpdater(guildId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SamuraiGuild that = (SamuraiGuild) o;

        if (guildId != that.guildId) return false;
        if (modules != that.modules) return false;
        return prefix.equals(that.prefix);
    }

    @Override
    public int hashCode() {
        int result = (int) (guildId ^ (guildId >>> 32));
        result = 31 * result + prefix.hashCode();
        result = 31 * result + (int) (modules ^ (modules >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SamuraiGuild{");
        sb.append("guildId=").append(guildId);
        sb.append(", prefix='").append(prefix).append('\'');
        sb.append(", players=").append(players);
        sb.append(", charts=").append(charts);
        sb.append(", channelModes=").append(channelModes);
        sb.append(", modules=").append(modules);
        sb.append('}');
        return sb.toString();
    }
}
