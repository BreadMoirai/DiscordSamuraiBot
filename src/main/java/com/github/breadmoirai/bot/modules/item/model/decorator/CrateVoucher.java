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
import com.github.breadmoirai.bot.modules.item.model.database.DropTable;
import com.github.breadmoirai.bot.modules.item.model.database.ItemDao;
import com.github.breadmoirai.bot.modules.item.model.database.ItemSlot;
import com.github.breadmoirai.breadbot.framework.Response;
import com.github.breadmoirai.breadbot.framework.response.Responses;
import com.github.breadmoirai.database.Database;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;


public class CrateVoucher extends ItemDecorator {
    public CrateVoucher(Item item) {
        super(item);
    }

    @Override
    protected Response use(ItemUseContext context) {
        if (!context.isBypassPrompt()) {
            return promptOpen(context);
        } else {
            return open(context);
        }
    }

    private Response promptOpen(ItemUseContext context) {
        final Emote emote = getData().getEmote();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final StringBuilder sb = embedBuilder.getDescriptionBuilder();
        final double v = getData().getProperties()[0];
        if (context.getPointModule().getPoints(context.getMember()) < v) {
            return Responses.of("This crate requires **" + v + "** points to open.");
        }
        sb.append("Pay **")
                .append(v)
                .append("** points to open ")
                .append(getData().getName());
        final Message redeemPrompt = new MessageBuilder().setEmbed(
                embedBuilder
                        .setTitle("Please confirm")
                        .setThumbnail(emote.getImageUrl())
                        .setColor(getData().getRarity().getColor())
                        .setFooter(String.format("SlotId: %d | ItemId: %d", context.getBaseSlot().getSlotId(), getData().getItemId()), null)
                        .build())
                .build();
        return Responses.newPrompt()
                .onYes(menu -> menu.replaceWith(open(context)), null)
                .buildResponse(redeemPrompt);
    }

    private Response open(ItemUseContext context) {
        final ItemSlot itemSlot = context.getBaseSlot();
        if (!context.getInventory().removeItemSlot(itemSlot)) return Responses.of("This item has already been used");
        final DropTable dropTable = Database.get().withExtension(ItemDao.class, itemDao -> itemDao.getDropTable(getData().getItemId()));
        final StringBuilder sb = new StringBuilder();
        context.getPointModule().offsetPoints(context.getMember(), getData().getProperties()[0] * -1);
        for (int i = 0; i < getData().getProperties()[1]; i++) {
            final Item drop = dropTable.getDrop();
            context.getInventory().addItem(drop);
            sb.append(drop.getData().getEmote().getAsMention());
        }
        return Responses.ofFormat("**%s** has paid %.2f points to open a %s_%s_ that contained %s", context.getMember().getEffectiveName(), getData().getProperties()[0], getData().getEmote().getAsMention(), getData().getName(), sb.toString());
    }
}
