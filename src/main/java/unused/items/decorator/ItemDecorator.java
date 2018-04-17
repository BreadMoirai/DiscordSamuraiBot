package com.github.breadmoirai.samurai.items.decorator;

import com.github.breadmoirai.samurai.items.Item;
import com.github.breadmoirai.samurai.items.ItemData;
import com.github.breadmoirai.samurai.items.ItemUseContext;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;

public abstract class ItemDecorator implements Item {

    private final Item baseItem;

    ItemDecorator(Item baseItem) {
        this.baseItem = baseItem;
    }

    @Override
    public final ItemData getData() {
        return baseItem.getData();
    }

    @Override
    public final SamuraiMessage useItem(ItemUseContext context) {
        final SamuraiMessage result = this.use(context);
        if (result == null)
            return baseItem.useItem(context);
        else
            return result;
    }

    protected abstract SamuraiMessage use(ItemUseContext context);

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + "{");
        sb.append(baseItem);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Item && getData().getItemId() == ((Item) o).getData().getItemId();
    }

    @Override
    public int hashCode() {
        return getData().hashCode();
    }

}
