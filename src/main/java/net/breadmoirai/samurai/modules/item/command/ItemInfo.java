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

import net.breadmoirai.samurai.modules.item.model.Item;
import net.breadmoirai.samurai.modules.item.model.database.ItemDao;
import net.breadmoirai.samurai.modules.item.model.data.ItemData;
import net.breadmoirai.samurai.modules.item.ItemModule;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.command.ModuleCommand;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.breadmoirai.sbf.database.Database;
import net.dv8tion.jda.core.EmbedBuilder;

@Key("item")
public class ItemInfo extends ModuleCommand<ItemModule> {

    @Override
    public Response execute(CommandEvent event, ItemModule module) {
        if (event.isNumeric()) {
            final int itemId = Integer.parseInt(event.getContent());
            final Item item = Database.get().withExtension(ItemDao.class, itemDao -> itemDao.selectItem(itemId));
            if (item == null) return Responses.of("No such item exists with that ID");
            final ItemData data = item.getData();
            final EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(data.getName())
                    .setThumbnail(item.getData().getEmote().getImageUrl())
                    .setFooter(String.valueOf(data.getItemId()), null)
                    .setColor(data.getRarity().getColor())
                    .setDescription(data.getDescription());
            return Responses.of(eb.build());
        } else return Responses.of("Please provide an Item ID");
    }
}
