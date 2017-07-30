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

public class ItemSlotBuilder {
    private long guildId;
    private long userId;
    private int slotId;
    private Item item;
    private int durability;

    public ItemSlotBuilder() {}

    public ItemSlotBuilder(ItemSlot slot) {
        guildId = slot.getGuildId();
        userId = slot.getUserId();
        slotId = slot.getSlotId();
        item = slot.getItem();
        durability = slot.getDurability();
    }

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

    public ItemSlotBuilder setDurability(int durability) {
        this.durability = durability;
        return this;
    }

    public ItemSlot createItemSlot() {
        final ItemSlot itemSlot = new ItemSlot(guildId, userId, slotId, item, durability);
        Database.get().useExtension(ItemDao.class, itemDao -> itemDao.insertItemSlot(itemSlot));
        return itemSlot;
    }

    public ItemSlot build() {
        return new ItemSlot(guildId, userId, slotId, item, durability);
    }
}