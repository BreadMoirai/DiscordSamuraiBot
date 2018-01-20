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

import com.github.breadmoirai.breadbot.framework.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.discord.bot.modules.item.model.data.ItemRarity;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ItemRarityInfo {

    @MainCommand
    public void rarity(CommandEvent event) {
        event.reply(Arrays.stream(ItemRarity.values()).map(itemRarity -> itemRarity.getEmote() + itemRarity.toString()).collect(Collectors.joining("\n")));
    }
}
