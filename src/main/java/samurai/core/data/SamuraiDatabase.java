package samurai.core.data;

import org.apache.commons.collections4.MapUtils;
import samurai.core.Bot;
import samurai.osu.BeatmapSet;
import samurai.osu.OsuJsonReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 4.x - 3/3/2017
 */
public class SamuraiDatabase {

    private static final ConcurrentHashMap<String, Integer> mapMD5;
    private static final ConcurrentHashMap<Integer, Integer> mapSetMap;

    private static final int HASH_SIZE = 32;

    static {
        mapMD5 = new ConcurrentHashMap<>();
        mapSetMap = new ConcurrentHashMap<>();
    }

    private SamuraiDatabase() {
    }

    public static void put(int mapId, int setId) {
        mapSetMap.put(mapId, setId);
    }

    public static void put(String hash, int mapId) {
        mapMD5.put(hash, mapId);
    }

    public static BeatmapSet getSet(String hash) {
        if (mapMD5.containsKey(hash))
            return getSet(mapMD5.get(hash));
        BeatmapSet set = new BeatmapSet(OsuJsonReader.getBeatmapSetArrayFromMap(hash));
        SamuraiStore.writeSet(set);
        return set;
    }

    public static BeatmapSet getSet(int mapId) {
        final Integer setId = mapSetMap.get(mapId);
        //noinspection ConstantConditions
        if (!SamuraiStore.getSetFile(setId).exists()) {
            return SamuraiStore.readSet(setId);
        } else {
            BeatmapSet set = new BeatmapSet(OsuJsonReader.getBeatmapSetArrayFromMap(mapId));
            SamuraiStore.writeSet(set);
            return set;
        }
    }

    /**
     * File Format for each set
     * <ul>
     * <li>SetSize - 1 bytes</li>
     * <li>SetId - 4 bytes</li>
     * <li>MapIds - 4*SetSize bytes</li>
     * </ul>
     *
     * @return an array of bytes
     */
    public static byte[] toBytes() {
        int valsize = mapSetMap.values().size();
        Queue<ByteBuffer> bufferQueue = new LinkedList<>();
        Map<Integer, List<Integer>> writeMap = new HashMap<>(valsize);
        for (Map.Entry<Integer, Integer> e : mapSetMap.entrySet()) {
            if (!writeMap.containsKey(e.getValue())) {
                writeMap.put(e.getValue(), new LinkedList<>());
            }
            writeMap.get(e.getValue()).add(e.getKey());
        }
        Map<Integer, String> md5Map = MapUtils.invertMap(mapMD5);
        int capacity = 0;
        for (Map.Entry<Integer, List<Integer>> e : writeMap.entrySet()) {
            ByteBuffer buf = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES + (Integer.BYTES + HASH_SIZE) * e.getValue().size());
            buf.put((byte) e.getValue().size());
            buf.putInt(e.getKey());
            for (Integer i : e.getValue()) {
                buf.putInt(i);
                buf.put(StandardCharsets.UTF_8.encode(md5Map.get(i)));
            }
            bufferQueue.add(buf);
            capacity += buf.capacity();
        }
        ByteBuffer data = ByteBuffer.allocate(capacity);
        try {
            bufferQueue.forEach(buf -> data.put(buf.array()));
        } catch (BufferOverflowException e) {
            Bot.logError(e);
        }
        return data.array();
    }

    public static boolean initializeFromBytes(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        try {
            while (buf.remaining() > 0) {
                int ksize = buf.get();
                int setId = buf.getInt();
                for (int i = 0; i < ksize; i++) {
                    int mapId = buf.getInt();
                    mapSetMap.put(mapId, setId);
                    byte[] hashArray = new byte[HASH_SIZE];
                    buf.get(hashArray);
                    mapMD5.put(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(hashArray)).toString(), mapId);
                }
            }
        } catch (BufferUnderflowException e) {
            Bot.logError(e);
            return false;
        }
        return true;
    }


    public static void read() {
        byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(SamuraiDatabase.class.getResource("./MapSetIds.db").toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        if (!SamuraiDatabase.initializeFromBytes(data)) Bot.log("Failed to initializeFromBytes Samurai Database");
        else Bot.log("Successfully initialized Database");
    }

    public static void write() {
        try {
            Files.write(Paths.get(SamuraiStore.class.getResource("./MapSetIds.db").toURI()), SamuraiDatabase.toBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Database Write Success");
        } catch (ClosedByInterruptException e) {
            System.err.println("Write operation interrupted. Could not write SamuraiDatabase.");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
