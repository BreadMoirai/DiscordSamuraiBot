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
import com.github.breadmoirai.bot.framework.core.command.BiModuleCommand;
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.impl.Response;
import com.github.breadmoirai.bot.framework.core.response.Responses;
import com.github.breadmoirai.bot.framework.core.response.simple.StringResponse;
import com.github.breadmoirai.bot.modules.item.ItemModule;
import com.github.breadmoirai.bot.modules.item.ItemUseContextBuilder;
import com.github.breadmoirai.bot.modules.item.model.Item;
import com.github.breadmoirai.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.bot.modules.item.model.database.ItemFactory;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlot;
import com.github.breadmoirai.bot.modules.points.PointModule;
import net.dv8tion.jda.core.entities.Member;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Key({"use", "open"})
public class UseItem extends BiModuleCommand<ItemModule, PointModule> {

    private static Pattern itemName = Pattern.compile("([a-zA-Z\\s]+)(?: (?i)(?:on |with )([a-zA-Z\\s]+))?");
    private static Pattern itemNum = Pattern.compile("([0-9]+)(?: (?i)(?:on |with )?([0-9]+))?");

    @Override
    public void execute(CommandEvent event, ItemModule itemModule, PointModule pointModule) {
        final ItemUseContextBuilder iucb = new ItemUseContextBuilder();
        iucb.setKey(event.getKey().toLowerCase());
        iucb.setBypassPrompt(false);
        final Member author = event.getMember();
        iucb.setMember(author);
        final Inventory authorInventory = Inventory.ofMember(author);
        iucb.setInventory(authorInventory);
        iucb.setPointModule(pointModule);
        final String content = event.getContent().trim().toLowerCase();

        Response result = matchName(content, iucb, authorInventory);
        if (result == null) result = matchNum(content, iucb, authorInventory);
        if (result != null) event.replyWith(result);
        event.reply("Please provide a valid index starting at 1 of the intended item in your inventory or provide the name of an item.");
    }

    private Response matchNum(String content, ItemUseContextBuilder iucb, Inventory authorInventory) {
        final Matcher number = itemNum.matcher(content);
        if (!number.matches()) return null;
        final ItemSlot baseItemSlot;
        {
            final String baseItemIdx = number.group(1);
            if (baseItemIdx == null || baseItemIdx.isEmpty() || !CommandEvent.isNumber(baseItemIdx)) return null;
            baseItemSlot = authorInventory.getItemSlot(Integer.parseInt(baseItemIdx));
            if (baseItemSlot == null) return new StringResponse("There is no item in that slot");
            iucb.setBaseItemSlot(baseItemSlot);
        }
        {
            final String targetItemIdx = number.group(2);
            if (targetItemIdx != null && !targetItemIdx.isEmpty() && CommandEvent.isNumber(targetItemIdx)) {
               iucb.setTargetItemSlot(authorInventory.getItemSlot(Integer.parseInt(targetItemIdx)));
            }
        }
        final Response result = baseItemSlot.getItem().useItem(iucb.build());
        if (result == null) new StringResponse("Item" + baseItemSlot.getItem().print() + " cannot be used");
        return null;
    }

    private Response matchName(String content, ItemUseContextBuilder iucb, Inventory authorInventory) {
        Matcher name = itemName.matcher(content);
        if (!name.matches()) return null;
        final Optional<Item> baseItemOpt;
        {
            final String baseItemName = name.group(1);
            System.out.println("baseItemName = " + baseItemName);
            if (baseItemName == null) return null;
            baseItemOpt = ItemFactory.getByName(baseItemName);
            if (!baseItemOpt.isPresent()) return null;
        }
        final Optional<Item> targetItemOpt;
        {
            final String targetItemName = name.group(2);
            System.out.println("targetItemName = " + targetItemName);
            if (targetItemName != null && !targetItemName.isEmpty()) {
                ItemFactory.getByName(targetItemName).flatMap(authorInventory::findItemSlot).ifPresent(iucb::setTargetItemSlot);
            }
        }
        final Item item = baseItemOpt.get();
        final Optional<ItemSlot> itemSlotOptional = authorInventory.findItemSlot(item);
        if (!itemSlotOptional.isPresent()) return null;

        final ItemSlot itemSlot = itemSlotOptional.get();
        iucb.setBaseItemSlot(itemSlot);
        final Response use = itemSlot.getItem().useItem(iucb.build());
        if (use == null) return new StringResponse("Item" + itemSlot.getItem().print() + " cannot be used");
        return use;
    }


}

