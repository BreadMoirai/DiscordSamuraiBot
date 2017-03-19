package samurai.command;

import java.util.Comparator;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
public enum Commands {
    GenericCommand(0x0L),
    Info(0x1L),
    Link(0x2L),
    Profile(0x4L),
    RandomBeatmap(0x8L),
    Rank(0x10L),
    Upload(0x20L),
    Help(0x40L),
    Invite(0x80L),
    Join(0x100L),
    Menu(0x200L),
    Ping(0x400L),
    Enable(0x800L),
    Perm(0x1000L),
    Prefix(0x2000L),
    Status(0x4000L),
    Uptime(0x8000L),
    Casino(0x10000L),
    Duel(0x20000L),
    Hangman(0x40000L),
    Draw(0x80000L),
    ExampleCommand(0x100000L),
    Flame(0x200000L),
    ToList(0x400000L),
    Wasted(0x800000L),
    Groovy(0x1000000L),
    Purge(0x2000000L),
    Refresh(0x4000000L),
    Reset(0x8000000L),
    Shutdown(0x10000000L);


    private final long value;


    Commands(long value) {
        this.value = value;
    }

    public static long getEnabled(Commands... enabled) {
        long byteCombo = 0L;
        for (Commands cd : enabled) {
            byteCombo |= cd.value;
        }
        return byteCombo;
    }

    public static long getDefaultEnabledCommands() {
        return getEnabled(GenericCommand, Help, Invite, Join, RandomBeatmap, Info, Link, Profile, Rank, Upload, Enable, Perm, Prefix, Uptime);
    }

    public static long getEnabledAll() {
        return getEnabled(Commands.values());
    }

    public long getValue() {
        return value;
    }

    public boolean isEnabled(long byteCombo) {
        return (byteCombo & this.value) == this.value;
    }

    public static class CommandCP implements Comparator<Class<? extends Command>> {

        @Override
        public int compare(Class<? extends Command> o1, Class<? extends Command> o2) {
            final int i = packagePriority(o1.getCanonicalName()) - packagePriority(o2.getCanonicalName());
            if (i == 0) return o1.getSimpleName().compareTo(o2.getSimpleName());
            else return i;
        }

        private int packagePriority(String s) {
            if (s.contains("primary")) {
                return 1;
            } else if (s.contains("general")) {
                return 2;
            } else if (s.contains("manage")) {
                return 3;
            } else if (s.contains("hidden")) {
                return 4;
            } else if (s.contains("limited")) {
                return 5;
            } else if (s.contains("restricted")) {
                return 6;
            } else return 0;
        }
    }


}
