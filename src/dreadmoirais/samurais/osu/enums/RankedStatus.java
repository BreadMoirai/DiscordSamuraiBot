package dreadmoirais.samurais.osu.enums;

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
    LOVED (7);

    private final int value;

    RankedStatus(int value) {
        this.value = value;
    }

    int value() {
        return value;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().replaceAll("_", " "));
    }

    public static RankedStatus get(int value) {
        for ( RankedStatus status: RankedStatus.values()) {
            if (status.value() == value) {
                return status;
            }
        }
        return UNKNOWN;
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
