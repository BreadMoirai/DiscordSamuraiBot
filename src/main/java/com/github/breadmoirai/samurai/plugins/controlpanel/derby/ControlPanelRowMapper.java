/*
 *     Copyright 2017-2018 Ton Ly
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
 */

package com.github.breadmoirai.samurai.plugins.controlpanel.derby;

import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelBuilder;
import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControlPanelRowMapper implements RowMapper<ControlPanelBuilder> {

    @Override
    public ControlPanelBuilder map(ResultSet rs, StatementContext ctx) throws SQLException {
        final ControlPanelBuilder cpb = new ControlPanelBuilder();
        cpb.setId(rs.getInt(1));
        cpb.setGuildId(rs.getLong(2));
        cpb.setChannelId(rs.getLong(3));
        cpb.setMessageId(rs.getLong(4));
        final char type;
        try (Reader characterStream = rs.getCharacterStream(5)) {
            type = (char) (characterStream.read());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        cpb.setType(ControlPanelType.fromChar(type));
        return cpb;
    }
}
