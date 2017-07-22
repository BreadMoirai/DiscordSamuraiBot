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

package net.breadmoirai.samurai.modules.item.command;


import net.breadmoirai.samurai.modules.item.Inventory;
import net.breadmoirai.samurai.modules.item.ItemModule;

import net.breadmoirai.samurai.modules.item.ItemSlot;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.command.ModuleCommand;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.dv8tion.jda.core.entities.Member;

@Key("inventory")
public class InventoryInfo extends ModuleCommand<ItemModule> {

    @Override
    public Response execute(CommandEvent event, ItemModule module) {
        final Member member = event.getMember();
        final Inventory authorInventory = module.getInventory(member);
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("__**%s's Inventory**__", member.getEffectiveName()));
        int i = 0;
        for (ItemSlot itemSlot : authorInventory.getItemSlots()) {
            if (i++ % 10 == 0) sb.append('\n');
            sb.append(itemSlot.getItem().getData().getEmote().getAsMention());
        }
        return Responses.of(sb.toString());
    }
}
