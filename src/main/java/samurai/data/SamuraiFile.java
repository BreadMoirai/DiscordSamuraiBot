package samurai.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import samurai.osu.Score;
import samurai.osu.enums.GameMode;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TonTL on 1/27/2017.
 * Writes binary data to file
 */
public class SamuraiFile {

    // read functions
    public static char getToken(long guildId) {
        try {
            File file = new File(String.format("%s/%d.smrai", SamuraiFile.class.getResource("guild").getPath(), guildId));
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(file.length()-2);
            return raf.readChar();
        } catch (IOException e) {
            e.printStackTrace();
            return 0x00;
        }
    }

    public static Map<String, List<Score>> getScores(String path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "r");
        nextInt(raf);
        int count = nextInt(raf);
        Map<String, List<Score>> beatmapScores = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            String hash = nextString(raf);
            int scoreCount = nextInt(raf);
            List<Score> scoreList = new ArrayList<>(scoreCount);
            for (int j = 0; j < scoreCount; j++) {
                scoreList.add(nextScore(raf));
            }
            beatmapScores.put(hash, scoreList);
        }
        return beatmapScores;
    }

    private static Score nextScore(DataInput input) {
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
    private static byte nextByte(DataInput input) throws IOException {
        return input.readByte();
    }

    private static short nextShort(DataInput input) throws IOException {
        byte[] bytes = new byte[2];
        input.readFully(bytes);
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getShort();
    }

    private static int nextULEB128(DataInput input) throws IOException {
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

    private static String nextString(DataInput input) throws IOException {
        if (nextByte(input) == 0x0b) {
            int stringSize = nextULEB128(input);

            byte[] b = new byte[stringSize];
            input.readFully(b);
            return new String(b, "UTF-8");

        } else {
            return "Not Found";
        }
    }

    private static int nextInt(DataInput input) throws IOException {
        byte[] bytes = new byte[4];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    private static long nextLong(DataInput input) throws IOException {
        byte[] bytes = new byte[8];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getLong();
    }

    private static double nextDouble(DataInput input) throws IOException {
        byte[] bytes = new byte[8];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getDouble();
    }

    private static float nextSingle(DataInput input) throws IOException {
        byte[] bytes = new byte[4];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getFloat();
    }

    private static Map<Integer, Double> nextIntDoublePairs(DataInput input) throws IOException {
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

    private static void skip(DataInput input, int n) throws IOException {
        input.skipBytes(n);
    }


    //write functions
    public static void writeGuild(Guild guild) {
        System.out.println("Writing " + guild.getId());
        String path = SamuraiFile.class.getResource("guild").getPath();
        int userCount = guild.getMembers().size();
        List<Member> members = guild.getMembers();
        for (Member member : members) {
            if (member.getUser().isBot()) {
                userCount--;
            }
        }
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(String.format("%s/%s.smrai", path, guild.getId()))))) {
            // wait for jda update Guild#getIdLong
            writeLong(outputStream, Long.parseLong(guild.getId()));

            writeInt(outputStream, userCount);
            for (Member member : members) {
                if (!member.getUser().isBot())
                    writeLong(outputStream, Long.parseLong(member.getUser().getId()));
            }
            byte[] empty = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            for (int i = 0; i < userCount; i++) {
                outputStream.write(empty);
            }
            outputStream.write(0x21);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeInt(DataOutput output, int i) throws IOException {
        output.write(new byte[]{
                (byte) (0xff & i),
                (byte) (0xff & (i >> 8)),
                (byte) (0xff & (i >> 16)),
                (byte) (0xff & (i >> 24))
        });
    }

    private static void writeLong(DataOutput output, long l) throws IOException {
        output.write(new byte[]{
                (byte) (0xff & l),
                (byte) (0xff & (l >> 8)),
                (byte) (0xff & (l >> 16)),
                (byte) (0xff & (l >> 24)),
                (byte) (0xff & (l >> 32)),
                (byte) (0xff & (l >> 40)),
                (byte) (0xff & (l >> 48)),
                (byte) (0xff & (l >> 56)),
        });
    }


    /* DEBUGGING ONLY
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
    */

    private static String getLastModified(String path) {
        File file = new File(path);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(file.lastModified());
    }


}
