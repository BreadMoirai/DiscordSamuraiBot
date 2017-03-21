package samurai.data;

import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import samurai.osu.entities.BeatmapSet;
import samurai.util.OsuAPI;

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

    private static final ConcurrentHashMap<String, Integer> HASH_ID;
    private static final ConcurrentHashMap<Integer, String> ID_HASH;
    private static final ConcurrentHashMap<Integer, Integer> ID_SET;

    private static final int HASH_SIZE = 32;

    static {
        HASH_ID = new ConcurrentHashMap<>();
        ID_HASH = new ConcurrentHashMap<>();
        ID_SET = new ConcurrentHashMap<>();
    }

    private SamuraiDatabase() {
    }

    public static void put(int mapId, int setId) {
        ID_SET.put(mapId, setId);
    }

    public static void put(String hash, int mapId) {
        HASH_ID.put(hash, mapId);
        ID_HASH.put(mapId, hash);
    }

    public static String getHash(int mapId) {
        return ID_HASH.get(mapId);
    }

    public static BeatmapSet getSet(String hash) {
        if (HASH_ID.containsKey(hash))
            return getSet(HASH_ID.get(hash));
        final JSONArray beatmapSetArrayFromMap = OsuAPI.getBeatmapSetArrayFromMap(hash);
        if (beatmapSetArrayFromMap == null) return null;
        BeatmapSet set = new BeatmapSet(beatmapSetArrayFromMap);
        SamuraiStore.writeSet(set);
        return set;
    }

    public static BeatmapSet getSet(int mapId) {
        final Integer setId = ID_SET.get(mapId);
        //noinspection ConstantConditions
        if (!SamuraiStore.getSetFile(setId).exists()) {
            return SamuraiStore.readSet(setId);
        } else {
            BeatmapSet set = new BeatmapSet(OsuAPI.getBeatmapSetArrayFromMap(mapId));
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
    private static byte[] toBytes() {
        int valsize = ID_SET.values().size();
        Queue<ByteBuffer> bufferQueue = new LinkedList<>();
        Map<Integer, List<Integer>> writeMap = new HashMap<>(valsize);
        for (Map.Entry<Integer, Integer> e : ID_SET.entrySet()) {
            if (!writeMap.containsKey(e.getValue())) {
                writeMap.put(e.getValue(), new LinkedList<>());
            }
            writeMap.get(e.getValue()).add(e.getKey());
        }
        Map<Integer, String> md5Map = MapUtils.invertMap(HASH_ID);
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
            //Bot.logError(e);
        }
        return data.array();
    }

    private static boolean initializeFromBytes(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        try {
            while (buf.remaining() > 0) {
                int ksize = buf.get();
                int setId = buf.getInt();
                for (int i = 0; i < ksize; i++) {
                    int mapId = buf.getInt();
                    put(mapId, setId);
                    byte[] hashArray = new byte[HASH_SIZE];
                    buf.get(hashArray);
                    put(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(hashArray)).toString(), mapId);
                }
            }
        } catch (BufferUnderflowException e) {
            //Bot.logError(e);
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
        if (!SamuraiDatabase.initializeFromBytes(data))
            System.err.println("Failed to initializeFromBytes Samurai Database");
        else System.out.println("Successfully initialized Database");
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
