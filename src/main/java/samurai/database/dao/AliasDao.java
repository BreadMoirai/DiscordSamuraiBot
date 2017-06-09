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

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import samurai.database.MyStringJoiner;

import java.util.List;
import java.util.StringJoiner;

public interface AliasDao {
    @SqlUpdate("INSERT INTO CommandAlias VALUES (:id, :alias, :cmd)")
    void insertAlias(@Bind("id") long guildId, @Bind("alias") String alias, @Bind("cmd") String command);

    @SqlQuery("SELECT CommandAlias.Command FROM CommandAlias WHERE GuildId = :id AND Alias = :alias")
    String getAlias(@Bind("id") long guildId, @Bind("alias") String alias);

    @SqlUpdate("DELETE FROM CommandAlias WHERE GuildId = :id AND Alias = :alias")
    int deleteAlias(@Bind("id") long guildId, @Bind("alias") String alias);

    @RegisterRowMapper(MyStringJoiner.class)
    @SqlQuery("SELECT Alias, Command FROM CommandAlias WHERE GuildId = :id")
    List<String> getAllAliases(@Bind("id") long guildId);
}
