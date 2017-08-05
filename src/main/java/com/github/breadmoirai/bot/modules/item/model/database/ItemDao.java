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
package com.github.breadmoirai.bot.modules.item.model.database;

import com.github.breadmoirai.bot.modules.item.model.Item;
import net.dv8tion.jda.core.entities.Member;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface ItemDao {

    @SqlQuery("SELECT * FROM ItemCatalog WHERE ItemId = :id")
    @RegisterRowMapper(ItemFactory.class)
    Item selectItem(@Bind("id") int itemId);

    @SqlQuery("SELECT ItemCatalog.*, MemberInventory.SlotId, MemberInventory.Count, MemberInventory.UserId, MemberInventory.GuildId FROM MemberInventory JOIN ItemCatalog ON MemberInventory.ItemId = ItemCatalog.ItemId WHERE UserId = :uid AND GuildId = :gid")
    @RegisterRowMapper(ItemSlotMapper.class)
    List<ItemSlot> selectUserInventory(@Bind("gid") long guildId, @Bind("uid") long userId);

    @SqlUpdate("UPDATE MemberInventory SET Count = :count WHERE GuildId = :gid AND UserId = :uid AND SlotId = :slotId AND ItemId = :itemId")
    void updateItemSlotCount(@Bind("gid") long guildId, @Bind("uid") long userId, @Bind("slotId") int slotId, @Bind("itemId") int itemId, @Bind("count") int count);

    default void updateItemSlotCount(ItemSlot itemSlot) {
        updateItemSlotCount(itemSlot.getGuildId(), itemSlot.getUserId(), itemSlot.getSlotId(), itemSlot.getItem().getData().getItemId(), itemSlot.getDurability());
    }

    @SqlUpdate("DELETE FROM MemberInventory WHERE GuildId = :gid AND UserId = :uid AND SlotId = :slotId")
    void deleteItemSlot(@Bind("gid") long guildId, @Bind("uid") long userId, @Bind("slotId") int slotId);

    @SqlUpdate("INSERT INTO MemberInventory Values (:uid, :gid, :slotId, :itemId, :count)")
    void insertItemSlot(@Bind("gid") long guildId, @Bind("uid") long userId, @Bind("slotId") int slotId, @Bind("itemId") int itemId, @Bind("count") int count);

    default void insertItemSlot(ItemSlot itemSlot) {
        insertItemSlot(itemSlot.getGuildId(), itemSlot.getUserId(), itemSlot.getSlotId(), itemSlot.getItem().getData().getItemId(), itemSlot.getDurability());
    }

    @SqlQuery("SELECT ItemCatalog.*, MemberInventory.SlotId, MemberInventory.Count, MemberInventory.UserId, MemberInventory.GuildId FROM MemberInventory JOIN ItemCatalog ON MemberInventory.ItemId = ItemCatalog.ItemId WHERE UserId = :uid AND GuildId = :gid AND SlotId = :slotId")
    @RegisterRowMapper(ItemSlotMapper.class)
    ItemSlot selectItemSlot(@Bind("gid") long guildId, @Bind("uid") long userId, @Bind("slotId") int slotId);

    @SqlUpdate("DELETE FROM MemberInventory WHERE GuildId = :gid AND UserId = :uid")
    void deleteInventory(@Bind("gid") long guildId, @Bind("uid") long userId);

    @SqlUpdate("UPDATE MemberInventory SET SlotId = :newSlotId WHERE GuildId = :gid AND UserId = :uid AND SlotId = :oldSlotId AND ItemId = :itemId")
    void updateItemSlotId(@Bind("gid") long guildId, @Bind("uid") long userId, @Bind("newSlotId") int newSlotId, @Bind("itemId") int itemId, @Bind("oldSlotId") int oldSlotId);

    default void updateItemSlotId(ItemSlot itemSlot, int newSlotId) {
        updateItemSlotId(itemSlot.getGuildId(), itemSlot.getUserId(), newSlotId, itemSlot.getItem().getData().getItemId(), itemSlot.getSlotId());
    }

    @SqlQuery("SELECT DropId, Weight FROM DropRate WHERE ItemId = :id")
    @RegisterRowMapper(DropTable.class)
    List<int[]> selectDropTable(@Bind("id") int itemId);

    default DropTable getDropTable(int itemId) {
        return new DropTable(selectDropTable(itemId));
    }

    @SqlQuery("SELECT * FROM ItemCatalog WHERE Value > 0")
    @RegisterRowMapper(ItemFactory.class)
    List<Item> selectShopItems();

    @SqlQuery("SELECT * FROM ItemCatalog")
    @RegisterRowMapper(ItemFactory.class)
    List<Item> selectAllItems();

    @SqlQuery("SELECT * FROM ItemCatalog WHERE LOWER(Name) = :itemName")
    @RegisterRowMapper(ItemFactory.class)
    Item getByName(@Bind("itemName") String itemName);

    @SqlQuery("SELECT MAX(SlotId) FROM MemberInventory WHERE UserId = :uid AND GuildId = :gid")
    int getMaxItemSlot(@Bind("gid") long guildId, @Bind("uid") long userId);

    default int getMaxItemSlot(Member member) {
        return getMaxItemSlot(member.getGuild().getIdLong(), member.getUser().getIdLong());
    }
}
