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
    NONE (9);

    private static final String[] id = {"285320304618766346", "285320304610508800", "285320304618766346", "285320304614572042", "285320304476291074", "285320304354656267", "285320304392273921", "285320304614703104"};
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
        if (this==Grade.NONE) {
            return "";
        } else {
            return String.format("<:ranking%s:%s>", this.toString(), id[this.value()]);
        }
    }
}
