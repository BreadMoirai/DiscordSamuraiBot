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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.events.ReadyEvent;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import samurai.database.Database;
import samurai.database.dao.ItemDao;

import java.rmi.NoSuchObjectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ItemFactory implements RowMapper<Item>{

    private static final TIntObjectMap<Emote> itemEmotes = new TIntObjectHashMap<>();

    @Override
    public Item map(ResultSet rs, StatementContext ctx) throws SQLException {
        final ItemBuilder itemBuilder = new ItemBuilder();
        final int itemId = rs.getInt("ItemId");
        itemBuilder.setItemId(itemId);
        itemBuilder.setType(ItemType.valueOf(rs.getString("Type").toUpperCase()));
        itemBuilder.setName(rs.getString("Name"));
        itemBuilder.setRarity(ItemRarity.valueOf(rs.getShort("Rarity")));
        itemBuilder.setValue(rs.getDouble("Value"));
        itemBuilder.setDescription(rs.getString("Description"));
        itemBuilder.setStackLimit(/*rs.getShort("StackLimit")*/1);
        final double property[] = new double[6];
        final long property2[] = new long[2];
        property[0] = rs.getDouble("PropertyA");
        property[1] = rs.getDouble("PropertyB");
        property[2] = rs.getDouble("PropertyC");
        property[3] = rs.getDouble("PropertyD");
        property[4] = rs.getDouble("PropertyE");
        property[5] = rs.getDouble("PropertyF");
        property2[0] = rs.getLong("PropertyG");
        property2[1] = rs.getLong("PropertyH");
        itemBuilder.setProperties(property);
        itemBuilder.setProperties(property2);
        itemBuilder.setEmote(itemEmotes.get(itemId));
        return itemBuilder.createItem();
    }

    public static Item getItemById(int itemId) {
        return Database.get().<ItemDao, Item>openDao(ItemDao.class, itemDao -> itemDao.selectItem(itemId));
    }

    public static List<Item> getShopItems() {
        return Database.get().openDao(ItemDao.class, ItemDao::selectShopItems);
    }

    public static List<Item> getAllItems() { return Database.get().openDao(ItemDao.class, ItemDao::selectAllItems);}

    public static void load(ReadyEvent event) throws NoSuchObjectException {
        final JDA jda = event.getJDA();
        for (Item item : getAllItems()) {
            final Emote emote = jda.getEmoteById(item.getData().getProperties2()[1]);
            if (emote == null) throw new NoSuchObjectException("Emote for " + item.getData().getName() + " by id of " + item.getData().getProperties2()[1] + "was not found");
            else itemEmotes.put(item.getData().getItemId(), emote);
        }
    }
}
