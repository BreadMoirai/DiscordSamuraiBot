package com.github.breadmoirai.samurai.items;

import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;

public interface Item {

    SamuraiMessage useItem(ItemUseContext context);

    ItemData getData();

    default String print() {
        return getData().getEmote().getAsMention() + " " + getData().getName();
    }
}
