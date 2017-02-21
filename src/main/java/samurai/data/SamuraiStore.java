package samurai.data;

import net.dv8tion.jda.core.entities.Message;
import samurai.Bot;
import samurai.osu.Score;

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
@SuppressWarnings("Duplicates")
public class SamuraiStore {
    private static final int VERSION = 20170103;

    public static boolean containsGuild(long id) {
        return new File(getGuildDataPath(id)).exists();
    }

    public static boolean containsScores(long id) {
        return new File(getScoreDataPath(id)).exists();
    }

    private static String getGuildDataPath(long id) {
        return String.format("%s/%d.ser", SamuraiStore.class.getResource("guild").getPath(), id);
    }

    private static String getScoreDataPath(long id) {
        return String.format("%s/%d.db", SamuraiStore.class.getResource("score").getPath(), id);
    }

    public static String downloadFile(Message.Attachment attachment) {
        String path = String.format("%s/%s.db", SamuraiStore.class.getResource("temp").getPath(), attachment.getId());
        attachment.download(new File(path));
        return path;
    }

    public static String getHelp(String fileName) throws URISyntaxException {
        StringBuilder sb = new StringBuilder();
        if (SamuraiStore.class.getResource(String.format("./help/%s.txt", fileName)) == null)
            return String.format("`[prefix]%s` should be pretty self-explanatory... use `[prefix]join` if you need more help", fileName);
        try {
            Files.readAllLines(new File(SamuraiStore.class.getResource(String.format("./help/%s.txt", fileName)).toURI()).toPath(), StandardCharsets.UTF_8).forEach(line -> sb.append(line).append("\n"));
        } catch (URISyntaxException | IOException e) {
            Bot.logError(e);
        }
        return sb.toString();
    }

    public static void writeGuild(SamuraiGuild g) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(getGuildDataPath(g.getGuildId())))) {
            outputStream.writeObject(g);
        } catch (IOException e) {
            Bot.logError(e);
        }
        Bot.log("Success: GuildWrite - " + g.getGuildId());
    }

    public static SamuraiGuild readGuild(long guildId) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(getGuildDataPath(guildId)))) {
            SamuraiGuild g = (SamuraiGuild) input.readObject();
            if (g != null) Bot.log("Success: GuildRead - " + g.getGuildId());
            return g;
        } catch (IOException | ClassNotFoundException e) {
            Bot.logError(e);
            return null;
        }
    }

    public static boolean writeScoreData(long guildId, HashMap<String, LinkedList<Score>> scoreMap) {
        try (BufferedOutputStream out = new BufferedOutputStream(
                new DataOutputStream(
                        new FileOutputStream(getScoreDataPath(guildId).substring(3))))) {

            //Path path = Paths.get(getScoreDataPath(guildId).substring(3));
            ByteBuffer scoreDatabase = ByteBuffer.allocate(8);
            scoreDatabase.order(ByteOrder.LITTLE_ENDIAN);
            scoreDatabase.putInt(VERSION);
            scoreDatabase.putInt(scoreMap.keySet().size());
            //outputStream.write(scoreDatabase.array());
            out.write(scoreDatabase.array());
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
                out.write(beatmap.array());
                for (Score score : scoreList) {
                    out.write(score.toBytes());
                    scoreCount++;
                }
            }
            System.out.printf("%d scores written to %s%n", scoreCount, getScoreDataPath(guildId).substring(20));
            return true;
        } catch (IOException e) {
            Bot.logError(e);
            return false;
        }
    }
}
