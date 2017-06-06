/*    Copyright 2017 Ton Ly

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.items;

import samurai.database.Database;
import samurai.database.dao.ItemDao;
import samurai.util.ArrayUtil;

import java.util.*;

public class Inventory {
    private final long guildId, userId;
    private List<ItemSlot> itemSlots;

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
        if (itemSlots == null) {
            itemSlots = new ArrayList<>(Database.get().<ItemDao, List<ItemSlot>>openDao(ItemDao.class, itemDao -> itemDao.selectUserInventory(guildId, userId)));
            itemSlots.sort(ItemSlot.comparator());
        }
        return itemSlots;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Inventory{");
        sb.append("guildId=").append(guildId);
        sb.append(", userId=").append(userId);
        sb.append(", itemSlots=").append(itemSlots);
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
                itemSlot.add(count);
                return;
            }
        }
        int i = 1;
        for (ItemSlot itemSlot : getItemSlots()) {
            if (itemSlot.getSlotId() == i) {
                i++;
            } else {
                final ItemSlot firstEmptySlot = new ItemSlotBuilder().setGuildId(guildId).setUserId(userId).setSlotId(i).setItem(itemA).setCount(count).createItemSlot();
                getItemSlots().add(i - 1, firstEmptySlot);
                return;
            }
        }
        final ItemSlot firstEmptySlot = new ItemSlotBuilder().setGuildId(guildId).setUserId(userId).setSlotId(i).setItem(itemA).setCount(count).createItemSlot();
        getItemSlots().add(itemSlots.size(), firstEmptySlot);
    }

    public ItemSlot getItemSlot(int slotId) {
        if (itemSlots == null) {
            return Database.get().<ItemDao, ItemSlot>openDao(ItemDao.class, itemDao -> itemDao.selectItemSlot(guildId, userId, slotId));
        }else return getItemSlots().get(ArrayUtil.binarySearch(itemSlots, slotId, ItemSlot::getSlotId, Comparator.comparingInt(o -> o), null));
    }

    public void clear() {
        Database.get().<ItemDao>openDao(ItemDao.class, itemDao -> itemDao.deleteInventory(guildId, userId));
    }
}
