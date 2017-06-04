package samurai.items;

import java.awt.*;

public enum ItemRarity {
    TRIVIAL(new Color(144, 227, 210)),
    MUNDANE(new Color(119, 255, 108)),
    UNCOMMON(new Color(58, 137, 252)),
    SALIENT(new Color(180, 95, 246)),
    SUIGENERIS(new Color(255, 178, 36)),
    PREMIER(new Color(250, 55, 55)),
    EPOCHRIATIC(new Color(255, 92, 175));

    private Color color;

    ItemRarity(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static ItemRarity valueOf(short rarity) {
        switch (rarity) {
            case 1:
                return TRIVIAL;
            case 2:
                return MUNDANE;
            case 3:
                return UNCOMMON;
            case 4:
                return SALIENT;
            case 5:
                return SUIGENERIS;
            case 6:
                return PREMIER;
            case 7:
                return EPOCHRIATIC;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case TRIVIAL:
                return "Trivial";
            case MUNDANE:
                return "Mundane";
            case UNCOMMON:
                return "Uncommon";
            case SALIENT:
                return "Salient";
            case SUIGENERIS:
                return "Sui Generis";
            case PREMIER:
                return "Premier";
            case EPOCHRIATIC:
                return "Epochriatic";
            default:
                return null;
        }
    }
}
