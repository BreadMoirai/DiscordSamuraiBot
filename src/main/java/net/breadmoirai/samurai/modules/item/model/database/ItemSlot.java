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
package net.breadmoirai.samurai.modules.item.model.database;



import net.breadmoirai.samurai.modules.item.model.Item;
import net.breadmoirai.sbf.database.Database;

import java.util.Comparator;

public class ItemSlot {
    private final long guildId, userId;
    private int slotId;
    private final Item item;
    private int durability;

    ItemSlot(long guildId, long userId, int slotId, Item item, int count) {
        this.guildId = guildId;
        this.userId = userId;
        this.slotId = slotId;
        this.item = item;
        this.durability = count;
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

    public int getDurability() {
        return durability;
    }

    void setSlotId(int slotId) {
        if (this.slotId == slotId) return;
        Database.get().useExtension(ItemDao.class, itemDao -> itemDao.updateItemSlotId(this, slotId));
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
        if (durability != itemSlot.durability) return false;
        return item != null ? item.equals(itemSlot.item) : itemSlot.item == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (guildId ^ (guildId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + slotId;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + durability;
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
        sb.append(", durability=").append(durability);
        sb.append('}');
        return sb.toString();
    }

    public void destroy() {
        Database.get().useExtension(ItemDao.class, itemDao -> itemDao.deleteItemSlot(getGuildId(), getUserId(), getSlotId()));
    }
}
