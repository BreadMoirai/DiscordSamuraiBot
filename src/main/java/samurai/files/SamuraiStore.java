package samurai.files;

import net.dv8tion.jda.core.entities.Message;
import samurai.osu.entities.Score;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author TonTL
 * @version 4.5 - 2/20/2017
 */
public class SamuraiStore {
    public static final int VERSION = 20170103;

    public static String downloadFile(Message.Attachment attachment) {
        String path = String.format("%s/%s.db", SamuraiStore.class.getResource("temp").getPath(), attachment.getId());
        attachment.download(new File(path));
        return path;
    }

    public static String getHelp(String fileName) {
        StringBuilder sb = new StringBuilder();
        if (SamuraiStore.class.getResource(String.format("./help/%s.txt", fileName)) == null)
            return String.format("Nothing found for `%s`. Sorry!", fileName);
        try {
            Files.readAllLines(new File(SamuraiStore.class.getResource(String.format("./help/%s.txt", fileName)).toURI()).toPath(), StandardCharsets.UTF_8).forEach(line -> sb.append(line).append("\n"));
        } catch (URISyntaxException | IOException e) {
            //todo Bot.logError(e);
        }
        return sb.toString();
    }

    //score methods
    public static boolean containsScores(long id) {
        return new File(getScoreDataPath(id)).exists();
    }

    private static String getScoreDataPath(long id) {
        return String.format("%s/%d.db", SamuraiStore.class.getResource("score").getPath(), id);
    }

    public static boolean writeScoreData(long guildId, Map<String, List<Score>> scoreMap) {
        if (scoreMap.isEmpty()) return false;
        try (BufferedOutputStream out = new BufferedOutputStream(
                new DataOutputStream(
                        new FileOutputStream(getScoreDataPath(guildId).substring(3))))) {

            ByteBuffer scoreBuffer = ByteBuffer.allocate(8);
            scoreBuffer.order(ByteOrder.LITTLE_ENDIAN);
            scoreBuffer.putInt(VERSION);
            scoreBuffer.putInt(scoreMap.keySet().size());
            out.write(scoreBuffer.array());
            int scoreCount = 0;
            for (Map.Entry<String, List<Score>> entry : scoreMap.entrySet()) {
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
                out.write(beatmap.array());
                for (Score score : scoreList) {
                    out.write(score.toBytes());
                    scoreCount++;
                }
            }
            System.out.printf("%d scores written to %s%n", scoreCount, getScoreDataPath(guildId).substring(20));
            return true;
        } catch (IOException e) {
            //Bot.logError(e);
            return false;
        }
    }

    public static HashMap<String, LinkedList<Score>> readScores(long id) {
        return readScores(getScoreDataPath(id));
    }

    public static HashMap<String, LinkedList<Score>> readScores(String path) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path))) {
            int version = DbReader.nextInt(bis);
            System.out.println("version: " + version);
            if (version > VERSION) {
                System.out.println("NEW SCORE VERSION FOUND\n" + version + "\n");
            }
            int count = DbReader.nextInt(bis);
            HashMap<String, LinkedList<Score>> beatmapScores = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                String hash = DbReader.nextString(bis);
                int scoreCount = DbReader.nextInt(bis);
                LinkedList<Score> scoreList = new LinkedList<>();
                for (int j = 0; j < scoreCount; j++) {
                    scoreList.add(DbReader.nextScore(bis));
                }
                beatmapScores.put(hash, scoreList);
            }
            //Bot.log("Scores successfully read from " + path.substring(path.indexOf("data")));
            return beatmapScores;
        } catch (FileNotFoundException e) {
            //Bot.log("No Score File Found for ." + path.substring(path.length() - 28));
            return null;
        } catch (IOException e) {
            //Bot.logError(e);
            return null;
        }
    }

    public static File saveToFile(BufferedImage img, String filename) throws IOException {
        File file = getTempFile(filename);
        ImageIO.write(img, "jpg", file);
        return file;
    }

    private static File getTempFile(String filename) {
        return new File(SamuraiStore.class.getResource("temp").getPath() + '/' + filename);
    }

    public static BufferedImage getImage(String s) {
        try {
            return ImageIO.read(new File(SamuraiStore.class.getResource("images").getPath() + '/' + s));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
