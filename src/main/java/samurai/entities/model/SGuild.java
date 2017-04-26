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
package samurai.entities.model;

import samurai.database.Entry;
import samurai.entities.manager.GuildManager;
import samurai.osu.enums.GameMode;

import java.util.List;
import java.util.Optional;

/**
 * @author TonTL
 * @version 4/1/2017
 */
public interface SGuild {
    List<Player> getPlayers();

    Optional<Player> getPlayer(long discordId);

    int getPlayerCount();

    List<Chart> getCharts();

    long getGuildId();

    String getPrefix();

    long getEnabledCommands();

    List<Entry<Long, GameMode>> getChannelFilters();

    int getRankL(Player player);

    GuildManager getManager();
}
