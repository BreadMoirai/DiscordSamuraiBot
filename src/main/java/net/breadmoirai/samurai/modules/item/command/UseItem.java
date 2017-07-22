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

import net.breadmoirai.samurai.modules.item.*;
import net.breadmoirai.samurai.modules.item.decorator.PointVoucher;
import net.breadmoirai.samurai.modules.points.PointModule;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.BiModuleCommand;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.breadmoirai.sbf.core.response.simple.BasicResponse;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;
import java.util.StringJoiner;

@Key({"use", "open"})
public class UseItem extends BiModuleCommand<ItemModule, PointModule> {
  
    @Override
    public Response execute(CommandEvent event, ItemModule itemModule, PointModule pointModule) {
        final Member author = event.getMember();
        final Inventory authorInventory = itemModule.getInventory(author);
        if (event.isNumeric()) {
            final int i = Integer.parseInt(event.getContent());
            final ItemSlot itemSlot = authorInventory.getItemSlot(i);
            if (itemSlot == null) return Responses.of("There is no item in that slot");
            final Response use = itemSlot.getItem().useItem(
                    new ItemUseContextBuilder()
                            .setKey(event.getKey().toLowerCase())
                            .setMember(author)
                            .setInventory(authorInventory)
                            .setItemSlot(itemSlot)
                            .setSession(pointModule.getPointSession(author))
                            .build());
            if (use != null) return use;
            else
                return Responses.of("Item" + itemSlot.getItem().print() + " cannot be used");
        } else if (event.getContent().equalsIgnoreCase("vouchers")) {
            final StringJoiner stringJoiner = new StringJoiner("\n");
            final ItemUseContextBuilder iusb = new ItemUseContextBuilder()
                    .setKey(event.getKey().toLowerCase())
                    .setMember(author)
                    .setInventory(authorInventory)
                    .setSession(pointModule.getPointSession(author));
            final List<ItemSlot> itemSlots = authorInventory.getItemSlots();
            for (int i = itemSlots.size() - 1; i >= 0; i--) {
                ItemSlot itemSlot = itemSlots.get(i);
                if (itemSlot.getItem().getData().getType() == ItemType.CONSUMABLE && itemSlot.getItem() instanceof PointVoucher) {
                    final Response response = itemSlot.getItem().useItem(iusb.setItemSlot(itemSlot).build());
                    if (response != null && response instanceof BasicResponse)
                        stringJoiner.add(((BasicResponse) response).getMessage().getRawContent());
                }
            }
            stringJoiner.setEmptyValue("No items redeemed");
            return Responses.of(stringJoiner.toString());
        } else return Responses.of("Please provide a valid index starting at 1 of the intended item in your inventory");
    }
}
