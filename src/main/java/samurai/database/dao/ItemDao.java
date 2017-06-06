package samurai.database.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import samurai.items.*;

import java.util.List;

public interface ItemDao {

    @SqlQuery("SELECT * FROM ItemCatalog WHERE ItemId = :id")
    @RegisterRowMapper(ItemFactory.class)
    Item selectItem(@Bind("id") int itemId);

    @SqlQuery("SELECT ItemCatalog.*, MemberInventory.SlotId, MemberInventory.Count, MemberInventory.DiscordId, MemberInventory.GuildId FROM MemberInventory JOIN ItemCatalog ON MemberInventory.ItemId = ItemCatalog.ItemId WHERE DiscordId = :userId AND GuildId = :guildId")
    @RegisterRowMapper(ItemSlotMapper.class)
    List<ItemSlot> selectUserInventory(@Bind("guildId") long guildId, @Bind("userId") long userId);

    @SqlUpdate("UPDATE MemberInventory SET Count = :count WHERE GuildId = :guildId AND DiscordId = :userId AND SlotId = :slotId AND ItemId = :itemId")
    void updateItemSlotCount(@Bind("guildId") long guildId, @Bind("userId") long userId, @Bind("slotId") int slotId, @Bind("itemId") int itemId, @Bind("count") int count);

    default void updateItemSlotCount(ItemSlot itemSlot) {
        updateItemSlotCount(itemSlot.getGuildId(), itemSlot.getUserId(), itemSlot.getSlotId(), itemSlot.getItem().getData().getItemId(), itemSlot.getCount());
    }

    @SqlUpdate("DELETE FROM MemberInventory WHERE GuildId = :guildId AND DiscordId = :userId AND SlotId = :slotId")
    void deleteItemSlot(@Bind("guildId") long guildId, @Bind("userId") long userId, @Bind("slotId") int slotId);

    @SqlUpdate("INSERT INTO MemberInventory Values (:userId, :guildId, :slotId, :itemId, :count)")
    void insertItemSlot(@Bind("guildId") long guildId, @Bind("userId") long userId, @Bind("slotId") int slotId, @Bind("itemId") int itemId, @Bind("count") int count);

    default void insertItemSlot(ItemSlot itemSlot) {
        insertItemSlot(itemSlot.getGuildId(), itemSlot.getUserId() , itemSlot.getSlotId(), itemSlot.getItem().getData().getItemId(), itemSlot.getCount());
    }
    @SqlQuery("SELECT ItemCatalog.*, MemberInventory.SlotId, MemberInventory.Count, MemberInventory.DiscordId, MemberInventory.GuildId FROM MemberInventory JOIN ItemCatalog ON MemberInventory.ItemId = ItemCatalog.ItemId WHERE DiscordId = :userId AND GuildId = :guildId AND SlotId = :slotId")
    @RegisterRowMapper(ItemSlotMapper.class)
    ItemSlot selectItemSlot(@Bind("guildId") long guildId, @Bind("userId") long userId, @Bind("slotId") int slotId);

    @SqlUpdate("DELETE FROM MemberInventory WHERE GuildId = :guildId AND DiscordId = :userId")
    void deleteInventory(@Bind("guildId") long guildId, @Bind("userId") long userId);
}
