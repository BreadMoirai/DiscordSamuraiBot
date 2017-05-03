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

import org.apache.commons.lang3.text.WordUtils;
import samurai.database.objects.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        switch (value) {
            case 0: return STANDARD;
            case 1: return TAIKO;
            case 2: return CTB;
            case 3: return MANIA;
            default: return null;
        }
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name());
    }

    public byte toByte() {
        return (byte) value;
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

    public boolean tracks(Player player) {
        return (player.getModes() & bit()) == bit();
    }

    public short bit() {
        return (short) (0b0001 << value);
    }

    public static List<GameMode> getModes(short value) {
        return Arrays.stream(values()).filter(gameMode -> (value & gameMode.bit()) == gameMode.bit()).collect(Collectors.toList());
    }
}
