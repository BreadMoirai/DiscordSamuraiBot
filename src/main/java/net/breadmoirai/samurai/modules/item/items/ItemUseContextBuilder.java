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

import net.dv8tion.jda.core.entities.Member;
import samurai.points.PointSession;

import java.util.List;

public class ItemUseContextBuilder {
    private Member member;
    private PointSession session;
    private Inventory inventory;
    private String key;
    private List<Item> itemList;
    private ItemSlot itemSlot;

    public ItemUseContextBuilder setMember(Member member) {
        this.member = member;
        return this;
    }

    public ItemUseContextBuilder setSession(PointSession session) {
        this.session = session;
        return this;
    }

    public ItemUseContextBuilder setInventory(Inventory inventory) {
        this.inventory = inventory;
        return this;
    }

    public ItemUseContextBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ItemUseContextBuilder setItemList(List<Item> itemList) {
        this.itemList = itemList;
        return this;
    }

    public ItemUseContextBuilder setItemSlot(ItemSlot itemSlot) {
        this.itemSlot = itemSlot;
        return this;
    }

    public ItemUseContext build() {
        return new ItemUseContext(member, session, inventory, key, itemSlot, itemList);
    }
}