package samurai.items;

public class ItemBuilder {
    private int itemId;
    private ItemType type;
    private String name;
    private ItemRarity rarity;
    private double value;
    private double[] properties;
    private String description;

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

    public Item createItem() {
        switch (type) {

        }
        return new Item(itemId, type, name, rarity, value, properties, description);
    }
}