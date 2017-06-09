/*
 *       Copyright 2017 Ton Ly
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package samurai.command.items;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.items.Item;
import samurai.items.ItemFactory;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.stream.Collectors;

@Key({"shop", "buy"})
public class ItemShop extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.hasContent()) {
            int itemId = 0;
            if (context.isNumeric()) {
                itemId = Integer.parseInt(context.getContent());
            } else {
                switch (context.getContent().toLowerCase()) {
                    case "vip":
                        itemId = 200;
                }
            }
            if (itemId != 0) {
                final Item item = ItemFactory.getItemById(itemId);
                if (item == null) return FixedMessage.build("Specified item does not exist");
                else if (item.getData().getValue() == 0.0)
                    return FixedMessage.build(item.print() + " is not available for sale");
                if (context.getAuthorPoints().getPoints() < item.getData().getValue())
                    return FixedMessage.build(String.format("You require an additional **%.2f** points to buy %s", item.getData().getValue() - context.getAuthorPoints().getPoints(), item.print()));
                context.getAuthorPoints().offsetPoints(item.getData().getValue() * -1);
                context.getAuthorInventory().addItem(item);
                return FixedMessage.build("Thank you for your purchase of " + item.print());
            }
        }
        return FixedMessage.build("__**Items for Sale**__\n" + ItemFactory.getShopItems().stream().map(item -> String.format("%s - `%.0f`", item.print(), item.getData().getValue())).collect(Collectors.joining("\n")));
    }
}
