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

import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.Response;
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.command.ModuleCommand;
import com.github.breadmoirai.bot.framework.core.response.simple.StringResponse;
import com.github.breadmoirai.bot.modules.item.ItemModule;
import com.github.breadmoirai.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlot;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

@Key("inventory")
public class InventoryInfo extends ModuleCommand<ItemModule> {

    @Override
    public void execute(CommandEvent event, ItemModule module) {
        final Member member = event.getMember();
        final List<ItemSlot> itemSlots = Inventory.ofMember(member).getItemSlots();
        event.replyWith(displayInventory(member, itemSlots));
    }

    public Response displayInventory(Member member, List<ItemSlot> itemSlots) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("__**%s's Inventory**__", member.getEffectiveName()));
        int i = 0;
        for (ItemSlot itemSlot : itemSlots) {
            if (i++ % 10 == 0) sb.append('\n');
            sb.append(itemSlot.getItem().getData().getEmote().getAsMention());
        }
        return new StringResponse(sb.toString());
    }
}
