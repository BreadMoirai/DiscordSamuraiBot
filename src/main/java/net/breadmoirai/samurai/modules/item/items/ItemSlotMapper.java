/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.items.items;

import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemSlotMapper implements org.jdbi.v3.core.mapper.RowMapper<ItemSlot> {

    private ItemFactory itemFactory;

    {
        itemFactory = new ItemFactory();
    }

    @Override
    public ItemSlot map(ResultSet rs, StatementContext ctx) throws SQLException {
        final Item item = itemFactory.map(rs, ctx);
        final int slotId = rs.getInt("SlotId");
        final int count = rs.getInt("Count");
        final long userId = rs.getLong("DiscordId");
        final long guildId = rs.getLong("GuildId");
        return new ItemSlotBuilder().setGuildId(guildId).setUserId(userId).setSlotId(slotId).setItem(item).setCount(count).build();
    }
}
