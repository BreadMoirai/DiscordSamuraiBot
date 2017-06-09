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

import net.dv8tion.jda.core.EmbedBuilder;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.database.dao.ItemDao;
import samurai.items.Item;
import samurai.items.ItemData;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

@Key("item")
public class ItemInfo extends Command{

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.isNumeric()) {
            final int itemId = Integer.parseInt(context.getContent());
            final Item item = Database.get().<ItemDao, Item>openDao(ItemDao.class, itemDao -> itemDao.selectItem(itemId));
            if (item == null) return FixedMessage.build("No such item exists with that ID");
            final ItemData data = item.getData();
            final EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(data.getName())
                    .setThumbnail(item.getData().getEmote(context.getClient()).getImageUrl())
                    .setFooter(String.valueOf(data.getItemId()), null)
                    .setColor(data.getRarity().getColor())
                    .setDescription(data.getDescription());
            return FixedMessage.build(eb.build());
        } else return FixedMessage.build("Please provide an Item ID");
    }
}
