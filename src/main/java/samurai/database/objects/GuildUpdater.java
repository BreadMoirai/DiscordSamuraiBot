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
import samurai.database.dao.ChannelDao;
import samurai.database.dao.GuildDao;
import samurai.osu.enums.GameMode;

public class GuildUpdater {


    private final long guildId;

    GuildUpdater(long guildId) {
        this.guildId = guildId;
    }

    public static GuildUpdater of(long guildId) {
        return new GuildUpdater(guildId);
    }

    public void updatePrefix(String newPrefix) {
        Database.get().<GuildDao>openDao(GuildDao.class, guildDao -> guildDao.updatePrefix(guildId, newPrefix));
    }

    public void updateModules(long newModules) {
        Database.get().<GuildDao>openDao(GuildDao.class, guildDao -> guildDao.updateModules(guildId, newModules));
    }

    public void removeChannelMode(long channelId) {
        Database.get().<ChannelDao>openDao(ChannelDao.class, channelDao -> channelDao.deleteChannelMode(channelId));
    }

    public void setChannelMode(long channelId, GameMode mode) {
        Database.get().openDao(ChannelDao.class, channelDao -> {
            if (channelDao.hasChannel(channelId)) {
                channelDao.updateChannelMode(channelId, mode.bit());
            }
        });
    }

    public void addChannelMode(long channelId, GameMode mode) {
        Database.get().openDao(ChannelDao.class, channelDao -> {
            final Pair<Long, Short> channelMode = channelDao.getChannelMode(channelId);
            if (channelMode != null) {
                channelDao.updateChannelMode(channelId, (short) (channelMode.getValue() | mode.bit()));
            }
        });
    }

    public void destroy() {
        Database.get().<GuildDao>openDao(GuildDao.class, guildDao -> guildDao.destroyGuild(guildId));
    }

    public void addPlayer(Player playerCreated, short mode) {
        Database.get().<GuildDao>openDao(GuildDao.class, guildDao -> guildDao.insertPlayerAssociation(guildId, playerCreated.getDiscordId(), mode));
    }
}
