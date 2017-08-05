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
import com.github.breadmoirai.bot.framework.core.command.Key;
import com.github.breadmoirai.bot.framework.core.command.ModuleCommand;
import com.github.breadmoirai.bot.framework.modules.owner.Owner;
import com.github.breadmoirai.bot.modules.item.ItemModule;
import com.github.breadmoirai.bot.modules.item.model.Item;
import com.github.breadmoirai.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.bot.modules.item.model.database.ItemFactory;

import java.util.Optional;

@Owner
@Key("/give")
public class GiveItem extends ModuleCommand<ItemModule> {
    @Override
    public void execute(CommandEvent event, ItemModule module) {
        if (event.isNumeric()) {
            final String item = event.getContent();
            if (CommandEvent.isNumber(item)) {
                final int itemId = Integer.parseInt(item);
                Inventory.ofMember(event.getMember()).addItem(ItemFactory.getItemById(itemId));
            }
        } else {
            final Optional<Item> item = ItemFactory.getByName(event.getContent().toLowerCase());
            final Inventory inventory = Inventory.ofMember(event.getMember());
            item.ifPresent(inventory::addItem);
        }
    }
}
