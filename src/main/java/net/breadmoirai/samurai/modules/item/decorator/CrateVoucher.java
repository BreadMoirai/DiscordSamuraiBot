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
package net.breadmoirai.samurai.modules.item.decorator;

import net.breadmoirai.samurai.modules.item.*;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.breadmoirai.sbf.database.Database;
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
        final Emote emote = getData().getEmote();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final StringBuilder sb = embedBuilder.getDescriptionBuilder();
        final double v = getData().getProperties()[0];
        if (context.getPointSession().getPoints() < v) {
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
                        .setFooter(String.format("SlotId: %d | ItemId: %d", context.getItemSlot().getSlotId(), getData().getItemId()), null)
                        .build())
                .build();
        return Responses.newPrompt()
                .onYes(menu -> menu.replaceWith(open(context)), null)
                .buildResponse(redeemPrompt);

    }

    private Response open(ItemUseContext context) {
        final ItemSlot itemSlot = context.getItemSlot();
        if (itemSlot.getCount() == 1) {
            if (!context.getInventory().removeItemSlot(itemSlot)) return Responses.of("This item has already been used");
        } else {
            itemSlot.offset(-1);
        }
        final DropTable dropTable = Database.get().withExtension(ItemDao.class, itemDao -> itemDao.getDropTable(getData().getItemId()));
        final StringBuilder sb = new StringBuilder();
        context.getPointSession().offsetPoints(getData().getProperties()[0] * -1);
        for (int i = 0; i < getData().getProperties()[1]; i++) {
            final Item drop = dropTable.getDrop();
            context.getInventory().addItem(drop);
            sb.append(drop.getData().getEmote().getAsMention());
        }
        return Responses.ofFormat("**%s** has paid %.2f points to open a %s_%s_ that contained %s", context.getMember().getEffectiveName(),getData().getProperties()[0], getData().getEmote().getAsMention(), getData().getName(), sb.toString());
    }
}
