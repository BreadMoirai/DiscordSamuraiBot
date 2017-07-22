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

import samurai.database.Database;
import samurai.database.dao.ItemDao;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final long guildId, userId;

    public static Inventory ofMember(long guildId, long userId) {
        return new Inventory(guildId, userId);
    }

    private Inventory(long guildId, long userId) {
        this.guildId = guildId;
        this.userId = userId;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getUserId() {
        return userId;
    }

    public List<ItemSlot> getItemSlots() {
        final ArrayList<ItemSlot> itemSlots = new ArrayList<>(Database.get().<ItemDao, List<ItemSlot>>openDao(ItemDao.class, itemDao -> itemDao.selectUserInventory(guildId, userId)));
        itemSlots.sort(ItemSlot.comparator());
        return itemSlots;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Inventory{");
        sb.append("guildId=").append(guildId);
        sb.append(", userId=").append(userId);
        sb.append(", itemSlots=").append(getItemSlots());
        sb.append('}');
        return sb.toString();
    }

    public void addItem(Item itemA) {
        addItem(itemA, 1);
    }

    public void addItem(Item itemA, int count) {
        for (ItemSlot itemSlot : getItemSlots()) {
            final Item item = itemSlot.getItem();
            if (item.equals(itemA) && itemSlot.getCount() + count <= item.getData().getStackLimit()) {
                itemSlot.offset(count);
                return;
            }
        }
        final ItemSlot newItemSlot = new ItemSlotBuilder().setGuildId(guildId).setUserId(userId).setSlotId(getItemSlots().size() + 1).setItem(itemA).setCount(count).createItemSlot();
        getItemSlots().add(newItemSlot);
    }

    public ItemSlot getItemSlot(int slotId) {
        final List<ItemSlot> itemSlots = getItemSlots();
        if (slotId - 1 < itemSlots.size())
            return itemSlots.get(slotId - 1);
        else return null;
    }

    public boolean removeItemSlot(ItemSlot slot) {
        final List<ItemSlot> itemSlots = getItemSlots();
        final boolean remove = itemSlots.remove(slot);
        if (remove) {
            slot.destroy();
            for (int i = 1; i <= itemSlots.size(); i++) {
                itemSlots.get(i - 1).setSlotId(i);
            }
        }
        return remove;
    }

    public void clear() {
        Database.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.deleteInventory(guildId, userId));
    }
}
