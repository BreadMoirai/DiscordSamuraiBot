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
package samurai.database.mapper;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import samurai.osu.enums.GameMode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChannelModeMapper implements RowMapper<Pair<Long, Short>> {

    @Override
    public Pair<Long, Short> map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new ImmutablePair<>(rs.getLong("ChannelID"), rs.getShort("Mode"));
    }
}
