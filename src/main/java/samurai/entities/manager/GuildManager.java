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
package samurai.entities.manager;

import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;

/**
 * @author TonTL
 * @version 4/3/2017
 */
public interface GuildManager {
    boolean addPlayer(Player p);

    boolean removePlayer(Player p);

    boolean addNewChart(String name, boolean isSet);

    boolean addChart(int chartId);

    SGuild getGuild();

    boolean setPrefix(String newPrefix);

    boolean setCommands(long newCommands);

    boolean addChannelFilter(long channelId, GameMode mode);

    void addPlayer(long authorId, String username, int user_id, double pp_raw, int pp_rank, int pp_country_rank);

    void setUsers(long... userID);

    void removeChannelFilter(long idLong);
}
