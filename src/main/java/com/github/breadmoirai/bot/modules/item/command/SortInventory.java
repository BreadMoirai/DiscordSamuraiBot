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
package com.github.breadmoirai.bot.modules.item.command;

import com.github.breadmoirai.bot.modules.item.ItemModule;
import com.github.breadmoirai.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlot;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlotBuilder;
import com.github.breadmoirai.breadbot.framework.command.Command;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SortInventory {

    @Command
    public void usebulk(CommandEvent event, ItemModule module) {
        final Member member = event.getMember();
        final Inventory inventory = Inventory.ofMember(member);
        final List<ItemSlot> itemSlots = inventory.getItemSlots();
        inventory.clear();
        itemSlots.sort((o1, o2) -> o2.getItem().getData().getItemId() - o1.getItem().getData().getItemId());
        AtomicInteger i = new AtomicInteger(1);
        for (ItemSlot itemSlot : itemSlots) {
            ItemSlotBuilder isb = new ItemSlotBuilder(itemSlot);
            isb.setSlotId(i.getAndIncrement());
            isb.createItemSlot();
        }
        event.replyWith(new InventoryInfo().displayInventory(member, itemSlots));
    }
}
