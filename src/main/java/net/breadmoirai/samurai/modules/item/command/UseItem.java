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

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.items.Inventory;
import samurai.items.ItemSlot;
import samurai.items.ItemType;
import samurai.items.ItemUseContextBuilder;
import samurai.items.decorator.PointVoucher;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.List;
import java.util.StringJoiner;

@Key({"use", "open"})
public class UseItem extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Inventory authorInventory = context.getAuthorInventory();
        if (context.isNumeric()) {
            final int i = Integer.parseInt(context.getContent());
            final ItemSlot itemSlot = authorInventory.getItemSlot(i);
            if (itemSlot == null) return FixedMessage.build("There is no item in that slot");
            final SamuraiMessage use = itemSlot.getItem().useItem(
                    new ItemUseContextBuilder()
                            .setKey(context.getKey().toLowerCase())
                            .setMember(context.getAuthor())
                            .setInventory(authorInventory)
                            .setItemSlot(itemSlot)
                            .setSession(context.getAuthorPoints())
                            .build());
            if (use != null) return use;
            else
                return FixedMessage.build("Item" + itemSlot.getItem().print() + " cannot be used");
        } else if (context.getContent().equalsIgnoreCase("vouchers")) {
            final StringJoiner stringJoiner = new StringJoiner("\n");
            final ItemUseContextBuilder iusb = new ItemUseContextBuilder()
                    .setKey(context.getKey().toLowerCase())
                    .setMember(context.getAuthor())
                    .setInventory(authorInventory)
                    .setSession(context.getAuthorPoints());
            final List<ItemSlot> itemSlots = authorInventory.getItemSlots();
            for (int i = itemSlots.size() - 1; i >= 0; i--) {
                ItemSlot itemSlot = itemSlots.get(i);
                if (itemSlot.getItem().getData().getType() == ItemType.CONSUMABLE && itemSlot.getItem() instanceof PointVoucher) {
                    final SamuraiMessage samuraiMessage = itemSlot.getItem().useItem(iusb.setItemSlot(itemSlot).build());
                    if (samuraiMessage != null && samuraiMessage instanceof FixedMessage)
                    stringJoiner.add(((FixedMessage) samuraiMessage).getMessage().getContentRaw());
                }
            }
            stringJoiner.setEmptyValue("No items redeemed");
            return FixedMessage.build(stringJoiner.toString());
        } else return FixedMessage.build("Please provide a valid index starting at 1 of the intended item in your inventory");
    }
}
