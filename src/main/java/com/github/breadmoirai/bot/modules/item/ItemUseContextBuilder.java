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
package com.github.breadmoirai.bot.modules.item;

import com.github.breadmoirai.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlot;
import com.github.breadmoirai.bot.modules.points.PointModule;
import net.dv8tion.jda.core.entities.Member;

public class ItemUseContextBuilder {
    private Member member;
    private PointModule pointModule;
    private Inventory inventory;
    private String key;
    private ItemSlot target;
    private ItemSlot base;
    private boolean bypassPrompt;

    public ItemUseContextBuilder setMember(Member member) {
        this.member = member;
        return this;
    }

    public ItemUseContextBuilder setPointModule(PointModule module) {
        this.pointModule = module;
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


    public ItemUseContextBuilder setBaseItemSlot(ItemSlot itemSlot) {
        this.base = itemSlot;
        return this;
    }

    public ItemUseContextBuilder setTargetItemSlot(ItemSlot target) {
        this.target = target;
        return this;
    }

    public ItemUseContextBuilder setBypassPrompt(boolean bypassPrompt) {
        this.bypassPrompt = bypassPrompt;
        return this;
    }

    public ItemUseContext build() {
        return new ItemUseContext(member, pointModule, inventory, key, base, target, bypassPrompt);
    }
}