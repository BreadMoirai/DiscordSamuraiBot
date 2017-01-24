package dreadmoirais.samurais.osu.enums;

import org.apache.commons.lang3.StringUtils;
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

    private final int value

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
}
