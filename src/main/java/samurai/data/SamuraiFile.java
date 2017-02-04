package samurai.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import samurai.osu.Score;
import samurai.osu.enums.GameMode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by TonTL on 1/27/2017.
 * Writes binary data to file
 */
public class SamuraiFile {

    //todo convert to threadsafe.

    private static final byte[] empty = new byte[]{
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};
    private static final List<String> dataNames = Arrays.asList("duels won", "duels fought", "commands used", "scores uploaded", "osu id");

    public static boolean addOsuScore(String path, long userId, boolean replace) {
        if (!replace && new File(getScorePath(userId)).exists()) {
            Map<String, List<Score>> scoreMap;
            try {
                scoreMap = SamuraiFile.getScores(getScorePath(userId));

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                byte[] data = Files.readAllBytes(Paths.get(path).toAbsolutePath());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(getScorePath(userId)));
                outputStream.write(data);
                outputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean writeScoreData(String path, Map<String, List<Score>> scoreMap) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(20150204);
            byteBuffer.putInt(scoreMap.keySet().size());
            for (String hash : scoreMap.keySet()) {
                ByteBuffer string = ByteBuffer.allocate(2 + hash.length() + Integer.BYTES);
                string.order(ByteOrder.LITTLE_ENDIAN);
                string.put((byte) 0x0b);
                string.put((byte) hash.length());
                for (int i = 0; i < hash.length(); i++) {
                    string.put(hash.charAt(i))
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String getScorePath(long userId) {
        return String.format("%s/%d.db", SamuraiFile.class.getResource("user").getPath(), userId);
    }

    public static void modifyUserData(long guildId, long userId, boolean replace, int value, String... dataField) {
        try (RandomAccessFile raf = new RandomAccessFile(new File(getGuildFilePath(guildId)), "rw")) {
            int dataFieldLength = dataField.length;
            int[] dataPoints = new int[dataFieldLength];
            for (int i = 0; i < dataFieldLength; i++) {
                dataPoints[i] = dataNames.indexOf(dataField[i]);
            }
            Arrays.sort(dataPoints);
            raf.seek(Integer.BYTES);
            int userCount = nextInt(raf);
            int userIndex = 0;
            // todo buffer this
            while (nextLong(raf) != userId) {
                userIndex++;
            }
            long userDataStart = 8 + (userCount * Long.BYTES) + (userIndex * dataNames.size() * Integer.BYTES);
            for (int dataPoint : dataPoints) {
                raf.seek(userDataStart + dataPoint * Integer.BYTES);
                if (replace) {
                    writeInt(raf, value);
                } else {
                    int before = nextInt(raf);
                    raf.seek(raf.getFilePointer() - Integer.BYTES);
                    writeInt(raf, before + value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getGuildFilePath(long Id) {
        return String.format("%s/%d.smrai", SamuraiFile.class.getResource("guild").getPath(), Id);
    }

    public static String downloadFile(Message.Attachment attachment) {
        String path = String.format("%s/%s.db", SamuraiFile.class.getResource("temp").getPath(), attachment.getId());
        attachment.download(new File(path));
        return path;
    }

    public static boolean hasFile(long guildId) {
        File file = new File(getGuildFilePath(guildId));
        return file.exists();
    }

    public static String getPrefix(long guildId) {
        try {
            File file = new File(getGuildFilePath(guildId));
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            byte[] prefix = new byte[Integer.BYTES];
            raf.read(prefix);
            raf.close();
            return new String(prefix).trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setPrefix(long guildId, String prefix) {
        try {
            File file = new File(getGuildFilePath(guildId));
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.write(String.format("%4s", prefix).getBytes());
            // remove debugging
            System.out.println(guildId + " set prefix to " + getPrefix(guildId));
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, List<Score>> getScores(String path) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        System.out.println("version: " + nextInt(bis));
        int count = nextInt(bis);
        Map<String, List<Score>> beatmapScores = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            String hash = nextString(bis);
            int scoreCount = nextInt(bis);
            List<Score> scoreList = new ArrayList<>(scoreCount);
            for (int j = 0; j < scoreCount; j++) {
                scoreList.add(nextScore(bis));
            }
            beatmapScores.put(hash, scoreList);
        }
        return beatmapScores;
    }

    private static Score nextScore(BufferedInputStream input) {
        Score score = null;
        try {
            score = new Score()
                    .setMode(GameMode.get(nextByte(input)))
                    .setVersion(nextInt(input))
                    .setBeatmapHash(nextString(input))
                    .setPlayer(nextString(input))
                    .setReplayHash(nextString(input))
                    .setCount300(nextShort(input))
                    .setCount100(nextShort(input))
                    .setCount50(nextShort(input))
                    .setGeki(nextShort(input))
                    .setKatu(nextShort(input))
                    .setCount0(nextShort(input))
                    .setScore(nextInt(input))
                    .setMaxCombo(nextShort(input))
                    .setPerfectCombo(nextByte(input) != 0x00)
                    .setModCombo(nextInt(input));
            nextString(input);
            score.setTimestamp(nextLong(input));
            skip(input, 4);
            score.setOnlineScoreID(nextLong(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }

    // todo convert to bitshifting
    private static byte nextByte(BufferedInputStream input) throws IOException {
        byte[] singleByte = new byte[1];
        input.read(singleByte);
        return singleByte[0];
    }

    private static short nextShort(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[2];
        input.read(bytes);
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getShort();
    }

    private static int nextULEB128(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[10];
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] = nextByte(input);
            if ((bytes[i] & 128) != 128) {
                break;
            }
        }
        int uleb = 0;
        for (int j = 0; j <= i; j++) {
            int b = bytes[j];
            b = (b & 127) << 7 * j;
            uleb = b ^ uleb;
        }
        return uleb;
    }

    private static String nextString(BufferedInputStream input) throws IOException {
        if (nextByte(input) == 0x0b) {
            int stringSize = nextULEB128(input);

            byte[] b = new byte[stringSize];
            input.read(b);
            return new String(b, "UTF-8");

        } else {
            return "Not Found";
        }
    }

    private static int nextInt(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[4];
        input.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    private static long nextLong(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[8];
        input.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getLong();
    }

    private static double nextDouble(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[8];
        input.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getDouble();
    }

    private static float nextSingle(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[4];
        input.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getFloat();
    }

    private static Map<Integer, Double> nextIntDoublePairs(BufferedInputStream input) throws IOException {
        int count = nextInt(input);
        Map<Integer, Double> pairs = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            skip(input, 1);
            int modCombo = nextInt(input);
            skip(input, 1);
            double starRating = nextDouble(input);
            pairs.put(modCombo, starRating);
        }
        return pairs;
    }

    private static void skip(BufferedInputStream input, int n) throws IOException {
        input.skip(n);
    }

    //write functions
    public static void writeGuild(Guild guild) {
        System.out.println("Writing " + guild.getId());
        // wait
        File file = new File(getGuildFilePath(Long.parseLong(guild.getId())));
        int userCount = guild.getMembers().size();
        List<Member> members = guild.getMembers();
        for (Member member : members) {
            if (member.getUser().isBot()) {
                userCount--;
            }
        }
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file))) {
            // wait for jda update Guild#getIdLong
            outputStream.write((String.format("%4s", "!").getBytes("UTF-8")));
            writeInt(outputStream, userCount);
            for (Member member : members) {
                if (!member.getUser().isBot())
                    // wait
                    writeLong(outputStream, Long.parseLong(member.getUser().getId()));
            }
            for (int i = 0; i < userCount; i++) {
                outputStream.write(empty);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeInt(DataOutput output, int i) throws IOException {
        output.writeInt(Integer.reverseBytes(i));
    }

    private static void writeLong(DataOutput output, long l) throws IOException {
        output.writeLong(Long.reverseBytes(l));
    }

    private static int nextInt(RandomAccessFile input) throws IOException {
        byte[] bytes = new byte[4];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    private static long nextLong(RandomAccessFile input) throws IOException {
        byte[] bytes = new byte[8];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getLong();
    }

    public static List<Data> getUserData(long guildId, long userId) {
        try (RandomAccessFile raf = new RandomAccessFile(new File(getGuildFilePath(guildId)), "r")) {
            raf.seek(Integer.BYTES);
            int userCount = nextInt(raf);
            int userIndex = 0;
            int dataStart = 8 + userCount * Long.BYTES;
            // todo buffer this
            while (nextLong(raf) != userId) {
                userIndex++;
            }
            raf.seek(dataStart + userIndex * dataNames.size() * 4);
            return new SamuraiFile().nextUserDataBuffered(raf);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getDataNames() {
        return dataNames;
    }

    // remove
    /* DEBUGGING ONLY
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
    */

    public static List<String> readTextFile(String fileName) {
        File textFile = new File(SamuraiFile.class.getResource(fileName).getPath());
        LinkedList<String> textLines = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            if (fileName.equals("todo.txt"))
                br.lines().forEach(textLines::addFirst);
            else br.lines().forEach(textLines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textLines;
    }

    public static void addTodo(String[] args) {
        File todoFile = new File(SamuraiFile.class.getResource("todo.txt").getPath());
        try (BufferedWriter output = new BufferedWriter(new FileWriter(todoFile, true))) {
            for (String s : args) {
                output.write(String.format("\n - %s", s.replace("_", " ")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Data> nextUserDataBuffered(DataInput input) throws IOException {
        byte[] userDataBytes = new byte[dataNames.size() * Integer.BYTES];
        input.readFully(userDataBytes);
        List<Data> userDataList = new LinkedList<>();
        for (int i = 0; i < userDataBytes.length; i += Integer.BYTES) {
            Integer value = (userDataBytes[i] & 0xff) +
                    ((userDataBytes[i + 1] & 0xff) << 8) +
                    ((userDataBytes[i + 2] & 0xff) << 16) +
                    ((userDataBytes[i + 3] & 0xff) << 24);
            userDataList.add(new Data(dataNames.get(i / Integer.BYTES), value));
        }
        return userDataList;
    }


    public class Data {
        public String name;
        public int value;

        Data(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

}
