package samurai.command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
public enum CommandModule {
    manage(0L),
    debug(1L),
    general(2L),
    osu(4L),
    music(8L),
    fun(16L),
    _____(32L),
    restricted(64L);


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

    public static long getDefaultEnabledCommands() {
        return getEnabled(manage);
    }

    public static long getEnabledAll() {
        return getEnabled(CommandModule.values());
    }

    public static List<CommandModule> getVisible() {
        return Arrays.stream(CommandModule.values()).filter(commandModule -> commandModule != restricted).collect(Collectors.toList());
    }

    public long getValue() {
        return value;
    }

    public boolean isEnabled(long byteCombo) {
        return (byteCombo & this.value) == this.value;
    }


}
