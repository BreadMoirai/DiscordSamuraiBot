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
package samurai.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CommandModule {
    basic(0L),
    manage(1L),
    general(2L),
    osu(4L),
    music(8L),
    fun(16L),
    points(32L),
    items(64L),
    voice(128L),
    restricted(256L),
    debug(512L);


    private final long value;


    CommandModule(long value) {
        this.value = value;
    }

    public static long getEnabled(CommandModule... enabled) {
        long byteCombo = 0L;
        for (CommandModule cd : enabled) {
            byteCombo |= cd.value;
        }
        return byteCombo;
    }

    public static long getEnabledAll() {
        return getEnabled(CommandModule.values());
    }

    public static List<CommandModule> getVisible() {
        return Arrays.asList(manage, general, osu, music, fun, points, items, voice);
    }

    public static long getDefault() {
        return getEnabled(getVisible().toArray(new CommandModule[8]));
    }

    public long getValue() {
        return value;
    }

    public boolean isEnabled(long byteCombo) {
        return (byteCombo & this.value) == this.value;
    }


}
