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

import samurai.database.Database;
import samurai.database.dao.PlayerDao;
import samurai.osu.SessionStats;

public class PlayerUpdater {

    private long discordId;

    public PlayerUpdater(long discordId) {
        this.discordId = discordId;
    }

    public SessionStats updateTo(Player newPlayer) {
        Database.get().<PlayerDao>openDao(PlayerDao.class, playerDao -> playerDao.updatePlayer(discordId, newPlayer));
        return null;
    }

    public void destroy() {
        Database.get().<PlayerDao>openDao(PlayerDao.class, playerDao -> playerDao.destroyPlayer(discordId));
    }

}
