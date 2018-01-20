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
package com.github.breadmoirai.discord.bot.modules.item.model;

import com.github.breadmoirai.breadbot.framework.response.CommandResponse;
import com.github.breadmoirai.discord.bot.modules.item.ItemUseContext;
import com.github.breadmoirai.discord.bot.modules.item.model.data.ItemData;

public interface Item {

    CommandResponse useItem(ItemUseContext context);

    ItemData getData();

    default String print() {
        return getData().getEmote().getAsMention() + " " + getData().getName();
    }
}
