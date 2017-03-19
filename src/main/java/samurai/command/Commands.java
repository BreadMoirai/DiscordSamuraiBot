package samurai.command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
public enum Commands {
    GenericCommand(0x0L),
    Info(1L),
    Link(2L),
    Profile(4L),
    RandomBeatmap(8L),
    Rank(16L),
    Upload(32L),
    Help(64L),
    Invite(128L),
    Join(256L),
    Menu(512L),
    Ping(1024L),
    Enable(2048L),
    Perm(4096L),
    Prefix(8192L),
    Status(16384L),
    Uptime(32768L),
    Casino(65536L),
    Duel(131072L),
    Flame(262144L),
    Hangman(524288L),
    Wasted(1048576L),
    Purge(2097152L),
    Refresh(4194304L),
    Reset(8388608L),
    Shutdown(16777216L),
    Draw(33554432L),
    ExampleCommand(67108864L),
    Groovy(134217728L),
    ToList(268435456L);


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

    public static List<Commands> getVisible() {
        return Arrays.stream(Commands.values()).filter(commands -> commands.value < Purge.value).collect(Collectors.toList());
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
            } else if (s.contains("debug")) {
                return 7;
            } else return 0;
        }
    }


}
