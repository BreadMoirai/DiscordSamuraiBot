package com.github.breadmoirai.samurai.items.decorator;

import com.github.breadmoirai.samurai.items.Item;
import com.github.breadmoirai.samurai.items.ItemRarity;
import com.github.breadmoirai.samurai.items.ItemSlot;
import com.github.breadmoirai.samurai.items.ItemUseContext;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import com.github.breadmoirai.samurai.messages.impl.util.Prompt;
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
    public SamuraiMessage use(ItemUseContext context) {
        if (getData().getRarity().compareTo(ItemRarity.SALIENT) > 0) {
            final Emote emote = getData().getEmote();
            final Message redeemPrompt = new MessageBuilder().setEmbed(
                    new EmbedBuilder()
                            .setTitle("Please confirm")
                            .setDescription("Redeem ")
                            .appendDescription(getData().getName())
                            .setThumbnail(emote.getImageUrl())
                            .setColor(getData().getRarity().getColor())
                            .setFooter(String.format("SlotId: %d | ItemId: %d", context.getItemSlot().getSlotId(), getData().getItemId()), null)
                            .build())
                    .build();
            return new Prompt(redeemPrompt, prompt -> {
                prompt.getChannel().deleteMessageById(prompt.getMessageId()).queue();
                prompt.getChannel().sendMessage(redeem(context).getMessage()).queue();
            }, prompt -> prompt.getJDA().getTextChannelById(prompt.getChannelId()).deleteMessageById(prompt.getMessageId()).queue());
        }
        return redeem(context);
    }

    private FixedMessage redeem(ItemUseContext context) {
        final ItemSlot itemSlot = context.getItemSlot();
        if (itemSlot.getCount() == 1) {
            if (!context.getInventory().removeItemSlot(itemSlot)) return FixedMessage.build("This item has already been used");
        } else {
            itemSlot.offset(-1);
        }
        final double[] properties = getData().getProperties();
        double value;
        if (properties[1] == 0.0) {
            value = properties[0];
        } else {
            value = ThreadLocalRandom.current().nextDouble(properties[0], properties[1]);
        }
        context.getPointSession().offsetPoints(value);
        return FixedMessage.build(String.format("**%s** redeemed a %s_%s_ for **%.2f** points", context.getMember().getEffectiveName(), getData().getEmote().getAsMention(), getData().getName(), value));
    }
}
