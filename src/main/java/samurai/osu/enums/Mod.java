/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
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
