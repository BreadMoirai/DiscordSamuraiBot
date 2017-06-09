package samurai.items;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;

import java.util.Arrays;

public class ItemData {
    private final int itemId, stackLimit;
    private final ItemRarity rarity;
    private final ItemType type;
    private final String name, description;
    private final double value, properties[];

    ItemData(int itemId, int stackLimit, ItemType type, String name, ItemRarity rarity, double value, double[] properties, String description) {
        this.itemId = itemId;
        this.stackLimit = stackLimit;
        this.rarity = rarity;
        this.type = type;
        this.name = name;
        this.description = description;
        this.value = value;
        this.properties = properties;
    }

    public int getItemId() {
        return itemId;
    }

    public ItemType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public double getValue() {
        return value;
    }

    public int getStackLimit() {
        return stackLimit;
    }

    public String getDescription() {
        return description;
    }

    public Emote getEmote(JDA client) {
        final Emote emote = client.getEmoteById((long) properties[7]);
        if (emote == null) throw new NullPointerException(name + ": emote not found with id = " + properties[7]);
        else return emote;
    }

    public double[] getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ItemData{");
        sb.append("itemId=").append(itemId);
        sb.append(", stackLimit=").append(stackLimit);
        sb.append(", rarity=").append(rarity);
        sb.append(", type=").append(type);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", value=").append(value);
        sb.append(", properties=").append(Arrays.toString(properties));
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemData itemData = (ItemData) o;

        return itemId == itemData.itemId;
    }

    @Override
    public int hashCode() {
        return itemId;
    }
}
