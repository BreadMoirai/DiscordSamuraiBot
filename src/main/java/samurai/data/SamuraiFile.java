package samurai.data;

import net.dv8tion.jda.core.entities.Message;
import samurai.Bot;
import samurai.osu.Score;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
public class SamuraiFile extends DbReader {
    private static final byte[] EMPTY_BYTES = {
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    private static final int VERSION = 20170103;

    public static String getHelp(String help) {
        StringBuilder sb = new StringBuilder();
        if (SamuraiFile.class.getResource(String.format("./help/%s.txt", help)) == null)
            return String.format("`[prefix]%s` should be pretty self-explanatory... use `[prefix]join` if you need more help", help);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(SamuraiFile.class.getResourceAsStream(String.format("./help/%s.txt", help))))) {
            br.lines().forEach(line -> sb.append(line).append("\n"));
        } catch (IOException e) {
            Bot.logError(e);
        }
        return sb.toString();
    }


    /**
     * current FileDataStructure
     * <ul>
     * <li>Prefix - 8 bytes</li>
     * <li>UserCount - 4 bytes</li>
     * <li>UserIds - 8*userCount</li>
     * <li>UserData - userCount * ↓
     * <ul>
     * <li>osuId - 4 bytes</li>
     * <li>osuName - 16 bytes</li>
     * <li>EmptyByte - 20 bytes</li>
     * </ul>
     * </li>
     * <li>ChartCount - 1 byte<ul>
     * <li>ChartId - 4 bytes</li>
     * <li>ChartNameLen - 1 byte</li>
     * <li>ChartName - NameLen bytes</li>
     * <li>ChartSize - 1 bytes [-1 for global]</li>
     * <li>MapIds - Size * ↓
     * <ul>
     * <li>id - 4 bytes [Negative is set, Positive is map]</li>
     * </ul>
     * </li>
     * </ul></ul>
     *
     * @param guild the object to be written
     */
    public static void writeGuildDataFrom(SamuraiGuild guild) {
        System.out.println("Writing " + guild);
        File file = new File(getGuildDataPath(guild.getGuildId()));
        Integer userCount = guild.getUserCount();
        ByteBuffer buffer = ByteBuffer.allocate(12 + userCount * (48));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(StandardCharsets.UTF_8.encode(String.format("%-8s", guild.getPrefix())));
        buffer.putInt(userCount);
        for (Long discordId : guild.getUserMap().keySet())
            buffer.putLong(discordId);
        for (SamuraiUser user : guild.getUserMap().values()) {
            buffer.putInt(user.getOsuId());
            buffer.put(StandardCharsets.UTF_8.encode(String.format("%-16s", user.getOsuName())));
            buffer.put(EMPTY_BYTES);
        }
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file))) {
            outputStream.write(buffer.array());
        } catch (IOException e) {
            Bot.logError(e);
        }
    }

    /**
     * * current FileDataStructure
     * <ul>
     * <li>Prefix - 8 bytes</li>
     * <li>UserCount - 4 bytes</li>
     * <li>UserIds - 8*userCount</li>
     * <li>UserData - userCount * ↓
     * <ul>
     * <li>osuId - 4 bytes</li>
     * <li>osuName - 16 bytes</li>
     * <li>EmptyByte - 20 bytes</li>
     * </ul>
     * </li>
     * <li>Charts<ul>
     * <li>Chart Id - 4 bytes</li>
     * <li>Chart NameLen - 1 byte</li>
     * <li>Chart Name - NameLen bytes</li>
     * <li>Chart Size - 1 bytes</li>
     * <li>Map Ids - Size * ↓
     * <ul>
     * <li>id - 4 bytes [Negative is set, Positive is map]</li>
     * </ul>
     * </li>
     * </ul></ul>
     * Pre-condition is that hasGuildData is called and file exists
     *
     * @param guild Data is written into this.
     */
    public static void readGuildDataInto(SamuraiGuild guild) {
        Bot.log("Reading data into " + guild.getGuildId());
        try (DataInputStream input =
                     new DataInputStream(
                             new BufferedInputStream(
                                     new FileInputStream(
                                             new File(getGuildDataPath(guild.getGuildId())))))) {
            byte[] bPrefix = new byte[8];
            input.readFully(bPrefix);
        } catch (IOException e) {
            Bot.logError(e);
        }
    }

    public static boolean writeScoreData(long guildId, HashMap<String, LinkedList<Score>> scoreMap) {
        try {
            // BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));
            Path path = Paths.get(String.format("%s/%d.db", SamuraiFile.class.getResource("score").getPath(), guildId).substring(3));
            ByteBuffer scoreDatabase = ByteBuffer.allocate(8);
            scoreDatabase.order(ByteOrder.LITTLE_ENDIAN);
            scoreDatabase.putInt(VERSION);
            scoreDatabase.putInt(scoreMap.keySet().size());
            //outputStream.write(scoreDatabase.array());
            Files.write(path, scoreDatabase.array());
            int scoreCount = 0;
            for (Map.Entry<String, LinkedList<Score>> entry : scoreMap.entrySet()) {
                String hash = entry.getKey();
                ByteBuffer beatmap = ByteBuffer.allocate(2 + hash.length() + Integer.BYTES);
                beatmap.order(ByteOrder.LITTLE_ENDIAN);
                beatmap.put((byte) 0x0b);
                beatmap.put((byte) hash.length());
                for (int i = 0; i < hash.length(); i++) {
                    beatmap.put((byte) hash.charAt(i));
                }
                List<Score> scoreList = entry.getValue();
                beatmap.putInt(scoreList.size());
                Files.write(path, beatmap.array(), StandardOpenOption.APPEND);
                for (Score score : scoreList) {
                    Files.write(path, score.toBytes(), StandardOpenOption.APPEND);
                    scoreCount++;
                }
            }
            System.out.printf("%d scores written to %s%n", scoreCount, path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//    public static void modifyUserData(long guildId, long userId, boolean replace, int value, String... dataField) {
//        try (RandomAccessFile raf = new RandomAccessFile(new File(getGuildDataPath(guildId)), "rw")) {
//            int dataFieldLength = dataField.length;
//            int[] dataPoints = new int[dataFieldLength];
//            for (int i = 0; i < dataFieldLength; i++) {
//                dataPoints[i] = DATA_NAMES.indexOf(dataField[i]);
//            }
//            Arrays.sort(dataPoints);
//            raf.seek(Integer.BYTES);
//            int userCount = nextInt(raf);
//            int userIndex = 0;
//            // todo buffer this
//            while (nextLong(raf) != userId) {
//                userIndex++;
//            }
//            long userDataStart = 8 + (userCount * Long.BYTES) + (userIndex * DATA_NAMES.size() * Integer.BYTES);
//            for (int dataPoint : dataPoints) {
//                raf.seek(userDataStart + dataPoint * Integer.BYTES);
//                if (replace) {
//                    writeInt(raf, value);
//                } else {
//                    int before = nextInt(raf);
//                    raf.seek(raf.getFilePointer() - Integer.BYTES);
//                    writeInt(raf, before + value);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static String getGuildDataPath(long Id) {
        return String.format("%s/%d.smrai", SamuraiFile.class.getResource("guild").getPath(), Id);
    }

    public static String downloadFile(Message.Attachment attachment) {
        String path = String.format("%s/%s.db", SamuraiFile.class.getResource("temp").getPath(), attachment.getId());
        attachment.download(new File(path));
        return path;
    }

    public static boolean hasFile(long guildId) {
        File file = new File(getGuildDataPath(guildId));
        return file.exists();
    }

    public static String getPrefix(long guildId) {
        try {
            File file = new File(getGuildDataPath(guildId));
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            byte[] prefix = new byte[8];
            if (raf.read(prefix) == -1) Bot.logError(new EOFException("Unexpected End of File"));
            raf.close();
            return new String(prefix, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setPrefix(long guildId, String prefix) {
        try {
            File file = new File(getGuildDataPath(guildId));
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.write(String.format("%4s", prefix).getBytes(StandardCharsets.UTF_8));
            // remove debugging
            System.out.println(guildId + " set prefix to " + getPrefix(guildId));
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static HashMap<String, LinkedList<Score>> getScores(String path) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        int version = nextInt(bis);
        System.out.println("version: " + version);
        if (version > VERSION) {
            System.out.println("NEW SCORE VERSION FOUND\n" + version + "\n");
        }
        int count = nextInt(bis);
        HashMap<String, LinkedList<Score>> beatmapScores = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            String hash = nextString(bis);
            int scoreCount = nextInt(bis);
            LinkedList<Score> scoreList = new LinkedList<>();
            for (int j = 0; j < scoreCount; j++) {
                scoreList.add(nextScore(bis));
            }
            beatmapScores.put(hash, scoreList);
        }
        return beatmapScores;
    }

    public static HashMap<String, LinkedList<Score>> getScores(long guildId) throws IOException {
        String path = String.format("%s/%d.db", SamuraiFile.class.getResource("score").getPath(), guildId);
        return getScores(path);
    }

    public static boolean hasScores(long guildId) {
        return new File(String.format("%s/%d.db", SamuraiFile.class.getResource("score").getPath(), guildId)).exists();
    }

//    public static List<Data> getUserData(long guildId, long userId, String... dataNames) {
//        try (RandomAccessFile raf = new RandomAccessFile(new File(getGuildDataPath(guildId)), "r")) {
//            raf.seek(Integer.BYTES);
//            int userCount = nextInt(raf);
//            int userIndex = 0;
//            int dataStart = 8 + userCount * Long.BYTES;
//            // todo buffer this
//            while (nextLong(raf) != userId) {
//                userIndex++;
//            }
//            if (dataNames.length == 0) {
//                raf.seek(dataStart + userIndex * DATA_NAMES.size() * 4);
//                return SamuraiFile.nextUserDataBuffered(raf);
//            } else {
//                List<Data> dataList = new ArrayList<>();
//                for (String name : dataNames) {
//                    int dataIndex = DATA_NAMES.indexOf(dataNames[0]);
//                    raf.seek(dataStart + userIndex * DATA_NAMES.size() * 4 + dataIndex * 4);
//                    dataList.add(new Data(name, nextInt(raf)));
//                }
//                return dataList;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public static List<String> getDataNames() {
//        return DATA_NAMES;
//    }

    public static List<String> readTextFile(String fileName) {
        File textFile = new File(SamuraiFile.class.getResource(fileName).getPath());
        LinkedList<String> textLines = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), StandardCharsets.UTF_8))) {
            if (fileName.equals("todo.txt"))
                br.lines().forEach(textLines::addFirst);
            else br.lines().forEach(textLines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textLines;
    }

//    public static void addTodo(String[] args) {
//        File todoFile = new File(SamuraiFile.class.getResource("todo.txt").getPath());
//        try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(todoFile, true), StandardCharsets.UTF_8))) {
//            for (String s : args) {
//                output.write(String.format("%n - %s", s.replace("_", " ")));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

//    private static List<Data> nextUserDataBuffered(DataInput input) throws IOException {
//        byte[] userDataBytes = new byte[DATA_NAMES.size() * Integer.BYTES];
//        input.readFully(userDataBytes);
//        List<Data> userDataList = new LinkedList<>();
//        for (int i = 0; i < userDataBytes.length; i += Integer.BYTES) {
//            Integer value = (userDataBytes[i] & 0xff) +
//                    ((userDataBytes[i + 1] & 0xff) << 8) +
//                    ((userDataBytes[i + 2] & 0xff) << 16) +
//                    ((userDataBytes[i + 3] & 0xff) << 24);
//            userDataList.add(new Data(DATA_NAMES.get(i / Integer.BYTES), value));
//        }
//        return userDataList;
//    }


}
