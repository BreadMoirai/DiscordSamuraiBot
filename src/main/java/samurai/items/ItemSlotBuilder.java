package samurai.items;

import samurai.database.Database;
import samurai.database.dao.ItemDao;

public class ItemSlotBuilder {
    private long guildId;
    private long userId;
    private int slotId;
    private Item item;
    private int count;

    public ItemSlotBuilder setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public ItemSlotBuilder setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public ItemSlotBuilder setSlotId(int slotId) {
        this.slotId = slotId;
        return this;
    }

    public ItemSlotBuilder setItem(Item item) {
        this.item = item;
        return this;
    }

    public ItemSlotBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    public ItemSlot createItemSlot() {
        final ItemSlot itemSlot = new ItemSlot(guildId, userId, slotId, item, count);
        Database.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.insertItemSlot(itemSlot));
        return itemSlot;
    }

    public ItemSlot build() {
        return new ItemSlot(guildId, userId, slotId, item, count);
    }
}