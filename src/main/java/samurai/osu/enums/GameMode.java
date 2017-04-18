package samurai.osu.enums;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Osu Game Mode from Osu!API
 * Created by TonTL on 1/23/2017.
 */
public enum GameMode {
    STANDARD(0),
    TAIKO(1),
    CTB(2),
    MANIA(3);


    private final int value;

    GameMode(int value) {
        this.value = value;
    }

    public static GameMode get(int value) {
        for (GameMode mode : GameMode.values()) {
            if (mode.value() == value) {
                return mode;
            }
        }
        return STANDARD;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name());
    }

    public static GameMode find(String s) {
        switch (s.toLowerCase()) {
            case "standard":
            case "std":
            case "osu":
                return STANDARD;
            case "taiko":
                return TAIKO;
            case "catch the beat":
            case "ctb":
                return CTB;
            case "osumania":
            case "mania":
                return MANIA;
            default:
                return null;
        }
    }
}
