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
package com.github.breadmoirai.bot.modules.item.model.decorator;

import com.github.breadmoirai.bot.modules.item.ItemUseContext;
import com.github.breadmoirai.bot.modules.item.model.Item;
import com.github.breadmoirai.bot.modules.item.model.data.ItemRarity;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlot;
import com.github.breadmoirai.breadbot.framework.Response;
import com.github.breadmoirai.breadbot.framework.response.Responses;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

public class PointVoucher extends ItemDecorator {

    public PointVoucher(Item baseItem) {
        super(baseItem);
    }

    @Override
    public Response use(ItemUseContext context) {
        if (!context.isBypassPrompt()) {
            if (getData().getRarity().compareTo(ItemRarity.EPIC) > 0) {
                return promptUse(context);
            }
        }
        return redeem(context);
    }

    private Response promptUse(ItemUseContext context) {
        final Emote emote = getData().getEmote();
        final Message redeemPrompt = new MessageBuilder().setEmbed(
                new EmbedBuilder()
                        .setTitle("Please confirm")
                        .setDescription("Redeem ")
                        .appendDescription(getData().getName())
                        .setThumbnail(emote.getImageUrl())
                        .setColor(getData().getRarity().getColor())
                        .setFooter(String.format("SlotId: %d | ItemId: %d", context.getBaseSlot().getSlotId(), getData().getItemId()), null)
                        .build())
                .build();
        return Responses.newPrompt()
                .onYes(menu -> menu.replaceWith(redeem(context)), null)
                .buildResponse(redeemPrompt);
    }

    private Response redeem(ItemUseContext context) {
        final ItemSlot itemSlot = context.getBaseSlot();
        if (!context.getInventory().removeItemSlot(itemSlot)) return Responses.of("This item has already been used");

        final double[] properties = getData().getProperties();
        double value;
        if (properties[1] == 0.0) {
            value = properties[0];
        } else {
            value = ThreadLocalRandom.current().nextDouble(properties[0], properties[1]);
        }
        context.getPointModule().offsetPoints(context.getMember(), value);
        return Responses.ofFormat("**%s** redeemed a %s_%s_ for **%.2f** points", context.getMember().getEffectiveName(), getData().getEmote().getAsMention(), getData().getName(), value);
    }
}
