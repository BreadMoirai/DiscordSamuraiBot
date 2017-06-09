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
import samurai.items.Inventory;
import samurai.items.ItemFactory;
import samurai.items.ItemSlot;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.StringJoiner;

@Key("inventory")
public class InventoryInfo extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Inventory authorInventory = context.getAuthorInventory();
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("__**%s's Inventory**__", context.getAuthor().getEffectiveName()));
        int i = 0;
        for (ItemSlot itemSlot : authorInventory.getItemSlots()) {
            if (i++ % 10 == 0) sb.append('\n');
            sb.append(itemSlot.getItem().getData().getEmote(context.getClient()).getAsMention());
        }
        return FixedMessage.build(sb.toString());
    }
}
