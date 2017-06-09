package samurai.items;

import net.dv8tion.jda.core.JDA;
import samurai.messages.base.SamuraiMessage;

public interface Item {

    SamuraiMessage useItem(ItemUseContext context);

    ItemData getData();

    default String print() {
        return getData().getEmote().getAsMention() + " " + getData().getName();
    }
}
