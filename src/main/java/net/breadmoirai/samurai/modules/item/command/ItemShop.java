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

import net.breadmoirai.samurai.modules.item.Item;
import net.breadmoirai.samurai.modules.item.ItemFactory;
import net.breadmoirai.samurai.modules.item.ItemModule;
import net.breadmoirai.samurai.modules.points.PointModule;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.BiModuleCommand;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;

import java.util.stream.Collectors;

@Key({"shop", "buy"})
public class ItemShop extends BiModuleCommand<ItemModule, PointModule> {

    @Override
    public Response execute(CommandEvent event, ItemModule itemModule, PointModule pointModule) {
        if (event.hasContent()) {
            int itemId = 0;
            if (event.isNumeric()) {
                itemId = Integer.parseInt(event.getContent());
            } else {
                switch (event.getContent().toLowerCase()) {
                    case "vip":
                        itemId = 200;
                }
            }
            if (itemId != 0) {
                final Item item = ItemFactory.getItemById(itemId);
                if (item == null) return Responses.of("Specified item does not exist");
                else if (item.getData().getValue() == 0.0)
                    return Responses.of(item.print() + " is not available for sale");
                if (pointModule.getPoints(event.getMember()) < item.getData().getValue())
                    return Responses.of(String.format("You require an additional **%.2f** points to buy %s", item.getData().getValue() - pointModule.getPointSession(event.getMember()).getPoints(), item.print()));
                pointModule.offsetPoints(event.getMember(),item.getData().getValue() * -1);
                itemModule.getInventory(event.getMember()).addItem(item);
                return Responses.of("Thank you for your purchase of " + item.print());
            }
        }
        return Responses.of("__**Items for Sale**__\n" + ItemFactory.getShopItems().stream().map(item -> String.format("%s - `%.0f`", item.print(), item.getData().getValue())).collect(Collectors.joining("\n")));
    }
}
