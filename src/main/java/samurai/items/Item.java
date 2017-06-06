package samurai.items;

import samurai.messages.base.SamuraiMessage;

public interface Item {

    SamuraiMessage useItem(ItemUseContext context);

    ItemData getData();
}
