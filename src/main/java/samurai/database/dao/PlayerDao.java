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
package samurai.database.dao;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import samurai.database.objects.Player;
import samurai.database.objects.PlayerBuilder;

public interface PlayerDao {

    @SqlUpdate("INSERT INTO Player VALUES (:discordId, :osuId, :osuName, :globalRank, :countryRank, :level, :rawPP, :accuracy, :playCount, :countX, :countS, :countA, :count300, :count100, :count50, :modes, :lastUpdated)")
    void insertPlayer(@BindBean Player player);

    @SqlUpdate("UPDATE Player SET OsuName = :osuName, GlobalRank = :globalRank, CountryRank = :countryRank, RawPP = :rawPP, Accuracy = :accuracy, PlayCount = :playCount, CountX = :countX, CountS = :countS, CountA = :countA, :count300, :count100, :count50, Modes = :modes, LastUpdated = :lastUpdated WHERE discordID = :id")
    void updatePlayer(@Bind("id") long discordId, @BindBean Player player);

    @SqlQuery("SELECT * FROM Player WHERE discordId = :id")
    @RegisterBeanMapper(PlayerBuilder.class)
    PlayerBuilder selectPlayer(@Bind("id") long discordId);
    
    default Player getPlayer(long discordId) {
        PlayerBuilder playerBuilder = selectPlayer(discordId);
        if (playerBuilder == null) return null;
        return playerBuilder.build();
    }


    @SqlUpdate("DELETE FROM Player WHERE discordId = :id")
    void deletePlayer(@Bind("id")long discordId);

    @SqlUpdate("DELETE FROM GuildPlayer WHERE discordId = :id")
    void deletePlayerAssociations(@Bind("id")long discordId);

    default void destroyPlayer(long discordId) {
        deletePlayerAssociations(discordId);
        deletePlayer(discordId);
    }
}
