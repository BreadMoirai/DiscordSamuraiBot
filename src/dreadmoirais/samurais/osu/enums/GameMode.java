package dreadmoirais.samurais.osu.enums;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by TonTL on 1/23/2017.
 */
public enum GameMode {
    OSU (0),
    TAIKO (1),
    CTB (2),
    MANIA (3);


    private int value;

    GameMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name());
    }

    public static GameMode get(int value) {
        for ( GameMode mode: GameMode.values()) {
            if (mode.value() == value) {
                return mode;
            }
        }
        return OSU;
    }
}
