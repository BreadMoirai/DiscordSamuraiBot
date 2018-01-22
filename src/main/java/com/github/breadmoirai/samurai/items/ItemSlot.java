package com.github.breadmoirai.samurai.items;

import com.github.breadmoirai.samurai.database.dao.ItemDao;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;

import java.util.Comparator;

public class ItemSlot {
    private final long guildId, userId;
    private int slotId;
    private final Item item;
    private int count;

    ItemSlot(long guildId, long userId, int slotId, Item item, int count) {
        this.guildId = guildId;
        this.userId = userId;
        this.slotId = slotId;
        this.item = item;
        this.count = count;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getUserId() {
        return userId;
    }

    public int getSlotId() {
        return slotId;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void offset(int count) {
        this.count += count;
        DerbyDatabase.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.updateItemSlotCount(this));
    }

    void setSlotId(int slotId) {
        if (this.slotId == slotId) return;
        DerbyDatabase.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.updateItemSlotId(this, slotId));
        this.slotId = slotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemSlot itemSlot = (ItemSlot) o;

        if (guildId != itemSlot.guildId) return false;
        if (userId != itemSlot.userId) return false;
        if (slotId != itemSlot.slotId) return false;
        if (count != itemSlot.count) return false;
        return item != null ? item.equals(itemSlot.item) : itemSlot.item == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (guildId ^ (guildId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + slotId;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + count;
        return result;
    }

    public static Comparator<ItemSlot> comparator() {
        return Comparator.comparingInt(ItemSlot::getSlotId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ItemSlot{");
        sb.append("slotId=").append(slotId);
        sb.append(", item=").append(item);
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }

    public void destroy() {
        DerbyDatabase.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.deleteItemSlot(getGuildId(), getUserId(), getSlotId()));
    }
}
