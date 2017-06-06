package samurai.items.decorator;

import samurai.items.Item;
import samurai.items.ItemUseContext;
import samurai.messages.base.SamuraiMessage;

public class CrateVoucher extends ItemDecorator {
    public CrateVoucher(Item item) {
        super(item);
    }

    @Override
    protected SamuraiMessage use(ItemUseContext context) {
        return null;
    }
}
