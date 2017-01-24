package dreadmoirais.samurais.osu.enums;

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
}
