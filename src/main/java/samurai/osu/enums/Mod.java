package samurai.osu.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Osu!Mods from Osu!API
 * Created by TonTL on 1/23/2017.
 */
public enum Mod {
    None (0),
    NoFail (1),
    Easy (2),
    //NoVideo (4),
    Hidden (8),
    HardRock (16),
    SuddenDeath (64),
    Relax (128),
    HalfTime (256),
    NightCore (512),
    Flashlight (1024),
    Auto (2048),
    SpunOut (4096),
    AutoPilot (8192),
    Perfect (16384);

    private final int value;

    Mod(int value) {
        this.value = value;
    }

    public static List<Mod> getMods(int modCombo) {
        List<Mod> mods = new ArrayList<>();
        if (modCombo == 0) {
            mods.add(Mod.None);
            return mods;
        }
        return Arrays.stream(Mod.values()).filter(mod -> (mod != None && (modCombo & mod.value) == mod.value)).collect(Collectors.toList());
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
