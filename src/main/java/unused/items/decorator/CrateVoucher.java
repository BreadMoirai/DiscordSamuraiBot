package com.github.breadmoirai.samurai.items.decorator;

import com.github.breadmoirai.samurai.database.dao.ItemDao;
import com.github.breadmoirai.samurai.items.DropTable;
import com.github.breadmoirai.samurai.items.Item;
import com.github.breadmoirai.samurai.items.ItemSlot;
import com.github.breadmoirai.samurai.items.ItemUseContext;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import com.github.breadmoirai.samurai.messages.impl.util.Prompt;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;

public class CrateVoucher extends ItemDecorator {
    public CrateVoucher(Item item) {
        super(item);
    }

    @Override
    protected SamuraiMessage use(ItemUseContext context) {
        final Emote emote = getData().getEmote();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final StringBuilder sb = embedBuilder.getDescriptionBuilder();
        final double v = getData().getProperties()[0];
        if (context.getPointSession().getPoints() < v) {
            return FixedMessage.build("This crate requires **" + v + "** points to open.");
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
        return new Prompt(redeemPrompt, prompt -> {
            prompt.getChannel().deleteMessageById(prompt.getMessageId()).queue();
            prompt.getChannel().sendMessage(open(context).getMessage()).queue();
        }, prompt -> prompt.getJDA().getTextChannelById(prompt.getChannelId()).deleteMessageById(prompt.getMessageId()).queue());
    }

    private FixedMessage open(ItemUseContext context) {
        final ItemSlot itemSlot = context.getItemSlot();
        if (itemSlot.getCount() == 1) {
            if (!context.getInventory().removeItemSlot(itemSlot)) return FixedMessage.build("This item has already been used");
        } else {
            itemSlot.offset(-1);
        }
        final DropTable dropTable = DerbyDatabase.get().<ItemDao, DropTable>openDao(ItemDao.class, itemDao -> itemDao.getDropTable(getData().getItemId()));
        final StringBuilder sb = new StringBuilder();
        context.getPointSession().offsetPoints(getData().getProperties()[0] * -1);
        for (int i = 0; i < getData().getProperties()[1]; i++) {
            final Item drop = dropTable.getDrop();
            context.getInventory().addItem(drop);
            sb.append(drop.getData().getEmote().getAsMention());
        }
        return FixedMessage.build(String.format("**%s** has paid %.2f points to open a %s_%s_ that contained %s", context.getMember().getEffectiveName(),getData().getProperties()[0], getData().getEmote().getAsMention(), getData().getName(), sb.toString()));
    }
}
