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
package com.github.breadmoirai.discord.bot.modules.item.command;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.response.CommandResponse;
import com.github.breadmoirai.discord.bot.IfAbsentReply;
import com.github.breadmoirai.discord.bot.modules.item.model.Item;
import com.github.breadmoirai.discord.bot.modules.item.model.data.ItemData;
import net.breadmoirai.sbf.core.response.Responses;
import net.dv8tion.jda.core.EmbedBuilder;

public class ItemInfo {

    @MainCommand
    public CommandResponse item(@IfAbsentReply("Please specify the ID of a valid item") Item item) {
        final ItemData data = item.getData();
        final EmbedBuilder eb = new EmbedBuilder()
                .setTitle(data.getName())
                .setThumbnail(data.getEmote().getImageUrl())
                .setFooter(String.valueOf(data.getItemId()), null)
                .setColor(data.getRarity().getColor())
                .setDescription(data.getDescription());
        return Responses.of(eb.build());
    }
}
