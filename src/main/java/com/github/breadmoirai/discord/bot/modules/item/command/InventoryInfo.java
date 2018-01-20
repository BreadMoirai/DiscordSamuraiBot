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
package com.github.breadmoirai.discord.bot.modules.item.command;

import com.github.breadmoirai.breadbot.framework.CommandEvent;
import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.response.CommandResponse;
import com.github.breadmoirai.breadbot.framework.response.Responses;
import com.github.breadmoirai.discord.bot.modules.item.ItemModule;
import com.github.breadmoirai.discord.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.discord.bot.modules.item.model.database.ItemSlot;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class InventoryInfo {

    @MainCommand
    public void inventory(CommandEvent event, ItemModule module) {
        final Member member = event.getMember();
        final List<ItemSlot> itemSlots = Inventory.ofMember(member).getItemSlots();
        event.replyWith(displayInventory(member, itemSlots));
    }

    public CommandResponse displayInventory(Member member, List<ItemSlot> itemSlots) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("__**%s's Inventory**__", member.getEffectiveName()));
        int i = 0;
        for (ItemSlot itemSlot : itemSlots) {
            if (i++ % 10 == 0) sb.append('\n');
            sb.append(itemSlot.getItem().getData().getEmote().getAsMention());
        }
        return Responses.of(sb.toString());
    }
}
