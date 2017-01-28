package samurai.osu.enums;

/**
 * Created by TonTL on 1/23/2017.
 */
public enum Grade {
    SSH (0),
    SS (1),
    SH (2),
    S (3),
    A (4),
    B (5),
    C (6),
    D (7),
    NONE (9);

    private int value;

    private static final String[] id = {"273365912348917760)","273365939829997568","273365958960087040", "273365977826328576", "273365998634008576", "273366010894221312", "273366026513547264", "273366042737246208"};

    Grade(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static Grade get(int value) {
        for ( Grade grade: Grade.values()) {
            if (grade.value() == value) {
                return grade;
            }
        }
        return NONE;
    }

    public String getEmote() {
        if (this==Grade.NONE) {
            return "";
        } else {
            return String.format("<:rank_%s:%s>", this.toString(), id[this.value()]);
        }
    }
}
