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

/**
 * Osu!Grade from Osu!API
 * Created by TonTL on 1/23/2017.
 */
public enum Grade {
    XH(0),
    X(1),
    SH (2),
    S (3),
    A (4),
    B (5),
    C (6),
    D (7),
    F (8),
    NONE (9);

    private static final String[] id = {"<:score_ssplus:291015373850017792>", "<:score_ss:291015373850017802>", "<:score_splus:291015373854212109>", "<:score_s:291015373367803915>", "<:score_a:291015373795622912>", "<:score_b:291015373632045057>", "<:score_c:291015373850148865>", "\uD83C\uDDE9" ,"<:score_f:291015373459816451>"};
    private final int value;

    Grade(int value) {
        this.value = value;
    }

    public static Grade get(int value) {
        for ( Grade grade: Grade.values()) {
            if (grade.value() == value) {
                return grade;
            }
        }
        return NONE;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public String getEmote() {
        return id[value];
    }
}
