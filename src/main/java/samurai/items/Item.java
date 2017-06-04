package samurai.items;

public class Item {
    private int itemId;
    private ItemRarity rarity;
    private ItemType type;
    private String name, description;
    private double value, properties[];

    public Item(int itemId, ItemType type, String name, ItemRarity rarity, double value, double[] properties, String description) {
        this.itemId = itemId;
        this.rarity = rarity;
        this.type = type;
        this.name = name;
        this.description = description;
        this.value = value;
        this.properties = properties;
    }
}
