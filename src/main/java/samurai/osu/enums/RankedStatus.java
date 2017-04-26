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

/**
 * Created by TonTL on 1/23/2017.
 * ranked status
 */
public enum RankedStatus {
    UNKNOWN (0),
    NOT_SUBMITTED (1),
    GRAVEYARD (2),
    RANKED (4),
    APPROVED (5),
    QUALIFIED (6),
    LOVED(7),
    PENDING(8);

    private final int value;

    RankedStatus(int value) {
        this.value = value;
    }

    public static RankedStatus get(int value) {
        for ( RankedStatus status: RankedStatus.values()) {
            if (status.value() == value) {
                return status;
            }
        }
        return UNKNOWN;
    }

    public static RankedStatus fromAPI(int approved) {
        switch (approved) {
            case -2:
                return GRAVEYARD;
            case -1:
                return NOT_SUBMITTED;
            case 0:
                return PENDING;
            case 1:
                return RANKED;
            case 2:
                return APPROVED;
            case 3:
                return QUALIFIED;
            case 4:
                return LOVED;
            default:
                return UNKNOWN;
        }
    }

    int value() {
        return value;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().replaceAll("_", " "));
    }

    public String getEmote() {
        if (value==4) {
            return "<:status_Ranked:273555598438825985>";
        } else if (value==5) {
            return "<:status_Approved:273555598501478400>";
        } else {
            return "";
        }
    }
}
