package dreadmoirais.samurais.osu.enums;

import java.util.ArrayList;
import java.util.List;

/**
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

    private int value;

    Mod(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static List<Mod> getMods(int modCombo) {
        List<Mod> mods = new ArrayList<>();
        if (modCombo == 0) {
            mods.add(Mod.None);
            return mods;
        }
        for (Mod mod : Mod.values()) {
            if ((modCombo & mod.value) == mod.value) {
                mods.add(mod);
            }
        }
        return mods;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
