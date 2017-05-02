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
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import samurai.database.objects.PlayerBean;
import samurai.database.objects.PlayerBuilder;

public interface PlayerDao {

    @SqlUpdate("INSERT INTO Player VALUES (:userId, :osuId, :osuName, :globalRank, :countryRank, :rawPP, :accuracy, :playCount, :lastUpdated)")
    void insertPlayer(@BindBean PlayerBean player);

    @SqlUpdate("UPDATE Player SET GlobalRank = :globalRank, CountryRank = :countryRank, RawPP = :rawPP, Accuracy = :accuracy, PlayCount = :playCount, LastUpdated = :lastUpdated WHERE userID = :userId")
    boolean update(@BindBean PlayerBean player);

    @SqlQuery("SELECT * FROM Player WHERE UserId = ?")
    @RegisterBeanMapper(PlayerBuilder.class)
    PlayerBuilder getById(long userId);

    @SqlUpdate("DELETE FROM Player WHERE userId = ?")
    void delete(long userId);
}
