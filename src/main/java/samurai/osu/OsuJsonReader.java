package samurai.osu;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONException;
import org.json.JSONObject;
import samurai.osu.enums.GameMode;
import samurai.osu.enums.Grade;
import samurai.osu.enums.RankedStatus;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TonTL on 1/23/2017.
 * Json
 */
public class OsuJsonReader {

    private static final String OSU_API = "https://samurai.osu.ppy.sh/api/";
    private static final String GET_USER = "get_user?", GET_BEATMAPS = "get_beatmaps?", GET_SCORES = "get_scores";
    private static final String KEY = "k=59258eb34b84d912c79cf1ecb7fc285b79e16194";

    OsuJsonReader() {
    }

    public static Message getUserInfo(String name) {
        List<JSONObject> json;
        try {
            json = readJsonFromUrl(OSU_API + GET_USER + KEY + "&type=string" + "&u=" + name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
        if (json.isEmpty()) {
            return null;
        }
        JSONObject profile = json.get(0);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(profile.getString("username"))
                .setUrl("https://samurai.osu.ppy.sh/u/" + profile.getString("username"))
                .setColor(Color.PINK)
                .setImage("http://s.ppy.sh/a/" + profile.get("user_id"))
                .addField("Level", profile.getString("level"), true)
                .addField("Rank", profile.getString("pp_rank").substring(0, 5), true)
                .addField("Play Count", profile.getString("playcount"), true)
                .addField("Accuracy", profile.getString("accuracy").substring(0, 5) + "%", true)
                .addField("Grades", String.format("%s%s                %s%s                %s%s", Grade.SS.getEmote(), profile.getString("count_rank_ss"), Grade.S.getEmote(), profile.getString("count_rank_s"), Grade.A.getEmote(), profile.getString("count_rank_a")), true)
                .setFooter("Osu!API", "http://w.ppy.sh/c/c9/Logo.png");
        return new MessageBuilder().setEmbed(eb.build()).build();
    }

    static Beatmap getBeatmapInfo(String hash) {
        List<JSONObject> json;
        try {
            json = readJsonFromUrl(OSU_API + GET_BEATMAPS + KEY + "&limit=1&h=" + hash);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (json.isEmpty()) {
            return null;
        }
        JSONObject info = json.get(0);
        Beatmap beatmap = new Beatmap();
        switch (info.getInt("approved")) {
            case (1):
                beatmap.setRankedStatus(RankedStatus.RANKED);
            case (2):
                beatmap.setRankedStatus(RankedStatus.APPROVED);
            default:
                beatmap.setRankedStatus(RankedStatus.UNKNOWN);
        }
        beatmap.setArtist(info.getString("artist"));
        beatmap.setMapID(info.getInt("beatmap_id"));
        beatmap.setMapID(info.getInt("beatmapset_id"));
        beatmap.setMapper(info.getString("creator"));
        beatmap.setDifficultyRating(info.getDouble("difficultyrating"));
        beatmap.setCs((float) info.getDouble("diff_size"));
        beatmap.setOd((float) info.getDouble("diff_overall"));
        beatmap.setAr((float) info.getDouble("diff_approach"));
        beatmap.setHp((float) info.getDouble("diff_drain"));
        beatmap.setDrainTime(info.getInt("hit_length"));
        beatmap.setTotalTime(info.getInt("total_length") * 1000);
        beatmap.setSource(info.getString("source"));
        beatmap.setSong(info.getString("title"));
        beatmap.setDifficulty(info.getString("version"));
        beatmap.setHash(hash);
        beatmap.setGameMode(GameMode.get(info.getInt("mode")));
        beatmap.setTags(info.getString("tags"));
        return beatmap;
    }

    private static List<String> readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        String text = sb.toString();
        try {
            if (text.charAt(0) != '[' || text.equals("[]")) {
                throw new Exception("Osu!API Error: " + text);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        text = text.substring(1, text.length() - 1);
        List<String> jsonStrings = new ArrayList<>();
        int splitter = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                splitter++;
            } else if (c == '}') {
                splitter--;
                if (splitter == 0) {
                    jsonStrings.add(text.substring(0, i + 1));
                    if (i + 2 < text.length()) {
                        text = text.substring(i + 2);
                        i = 0;
                    }
                }
            }
        }
        System.out.println(jsonStrings);
        return jsonStrings;
    }

    private static List<JSONObject> readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            List<JSONObject> json = new ArrayList<>();
            for (String jsonText : readAll(rd)) {
                json.add(new JSONObject(jsonText));
            }
            return json;
        }

    }
}

