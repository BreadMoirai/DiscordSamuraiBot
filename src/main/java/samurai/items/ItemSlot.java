package samurai.items;

import samurai.database.Database;
import samurai.database.dao.ItemDao;

import java.util.Comparator;

public class ItemSlot {
    private final long guildId, userId;
    private final int slotId;
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

    void add(int count) {
        this.count += count;
        Database.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.updateItemSlotCount(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemSlot itemSlot = (ItemSlot) o;

        return slotId == itemSlot.slotId;
    }

    @Override
    public int hashCode() {
        return slotId;
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
}
