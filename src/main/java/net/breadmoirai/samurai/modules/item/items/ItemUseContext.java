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

public class ItemUseContext {
    private Member member;
    private PointSession session;
    private Inventory inventory;
    private String key;
    private ItemSlot itemSlot;
    private List<Item> itemList;

    public ItemUseContext(Member member, PointSession session, Inventory inventory, String key, ItemSlot itemSlot, List<Item> itemList) {
        this.member = member;
        this.session = session;
        this.inventory = inventory;
        this.key = key;
        this.itemSlot = itemSlot;
        this.itemList = itemList;
    }

    public Member getMember() {
        return member;
    }

    public PointSession getPointSession() {
        return session;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getKey() {
        return key;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public ItemSlot getItemSlot() {
        return itemSlot;
    }
}
