package samurai.items.decorator;

import samurai.items.Item;
import samurai.items.ItemUseContext;
import samurai.messages.base.SamuraiMessage;

public class PointVoucher extends ItemDecorator{

    public PointVoucher(Item baseItem) {
        super(baseItem);
    }

    @Override
    public SamuraiMessage use(ItemUseContext context) {
        return null;
    }
}
