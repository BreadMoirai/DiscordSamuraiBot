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

public class ItemUseContext {
    private Member member;
    private PointModule module;
    private Inventory inventory;
    private String key;
    private ItemSlot baseSlot;
    private ItemSlot targetSlot;
    private boolean bypassPrompt;

    public ItemUseContext(Member member, PointModule module, Inventory inventory, String key, ItemSlot baseSlot, ItemSlot targetSlot, boolean bypassPrompt) {
        this.member = member;
        this.module = module;
        this.inventory = inventory;
        this.key = key;
        this.baseSlot = baseSlot;
        this.targetSlot = targetSlot;
        this.bypassPrompt = bypassPrompt;
    }

    public Member getMember() {
        return member;
    }

    public PointModule getPointModule() {
        return module;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getKey() {
        return key;
    }

    public ItemSlot getBaseSlot() {
        return baseSlot;
    }

    public ItemSlot getTargetSlot() {
        return targetSlot;
    }

    public boolean isBypassPrompt() {
        return bypassPrompt;
    }
}
