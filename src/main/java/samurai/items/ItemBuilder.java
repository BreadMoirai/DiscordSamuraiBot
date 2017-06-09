package samurai.items;

import net.dv8tion.jda.core.entities.Emote;
import samurai.items.decorator.CrateVoucher;
import samurai.items.decorator.PointVoucher;

public class ItemBuilder {
    private int itemId;
    private ItemType type;
    private String name;
    private ItemRarity rarity;
    private double value;
    private double[] properties;
    private String description;
    private int stackLimit;
    private long[] properties2;
    private Emote emote;

    public ItemBuilder setItemId(int itemId) {
        this.itemId = itemId;
        return this;
    }

    public ItemBuilder setType(ItemType type) {
        this.type = type;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setRarity(ItemRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public ItemBuilder setValue(double value) {
        this.value = value;
        return this;
    }

    public ItemBuilder setProperties(double[] properties) {
        this.properties = properties;
        return this;
    }

    public ItemBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public void setStackLimit(int stackLimit) {
        this.stackLimit = stackLimit;
    }

    public Item createItem() {
        final ItemData data = new ItemData(itemId, stackLimit, type, name, rarity, value, properties, properties2, description, emote);
        return build(data);
    }

    public Item build(ItemData data) {
        Item item = new BaseItem(data);
        final int itemId = data.getItemId();
        if (itemId > 100 && itemId <= 117) {
            item = new PointVoucher(item);
        } else if (itemId >= 300 && itemId <= 307) {
            item = new CrateVoucher(item);
        }
        return item;
    }

    public Item cloneItem(Item item) {
        return build(item.getData());
    }

    public void setProperties(long[] property2) {
        this.properties2 = property2;
    }

    public void setEmote(Emote emote) {
        this.emote = emote;
    }
}