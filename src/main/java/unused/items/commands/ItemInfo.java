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
package com.github.breadmoirai.samurai.command.items;

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.database.dao.ItemDao;
import com.github.breadmoirai.samurai.items.Item;
import com.github.breadmoirai.samurai.items.ItemData;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import net.dv8tion.jda.core.EmbedBuilder;

@Key("item")
public class ItemInfo extends Command{

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.isNumeric()) {
            final int itemId = Integer.parseInt(context.getContent());
            final Item item = DerbyDatabase.get().<ItemDao, Item>openDao(ItemDao.class, itemDao -> itemDao.selectItem(itemId));
            if (item == null) return FixedMessage.build("No such item exists with that ID");
            final ItemData data = item.getData();
            final EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(data.getName())
                    .setThumbnail(item.getData().getEmote().getImageUrl())
                    .setFooter(String.valueOf(data.getItemId()), null)
                    .setColor(data.getRarity().getColor())
                    .setDescription(data.getDescription());
            return FixedMessage.build(eb.build());
        } else return FixedMessage.build("Please provide an Item ID");
    }
}
