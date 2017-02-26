package samurai.osu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import samurai.Bot;
import samurai.osu.enums.GameMode;
import samurai.osu.enums.RankedStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TonTL on 1/23/2017.
 * Json
 */
public class OsuJsonReader {

    public static final AtomicInteger count;
    private static final String OSU_API;
    private static final String KEY;
    private static final String GET_USER;
    private static final String GET_BEATMAPS;
    private static final String GET_SCORES;

    static {
        count = new AtomicInteger(0);
        OSU_API = "https://osu.ppy.sh/api/";
        KEY = "k=59258eb34b84d912c79cf1ecb7fc285b79e16194";
        GET_USER = "get_user?";
        GET_BEATMAPS = "get_beatmaps?";
        GET_SCORES = "get_scores?";
    }

    public static JSONObject getUserJSON(String identity) {
        JSONArray json = readJsonFromUrl(String.format("%s%s%s&u=%s", OSU_API, GET_USER, KEY, identity));
        if (json == null || json.length() != 1) {
            return null;
        }
        return json.getJSONObject(0);
    }

    public static Beatmap getBeatmapInfo(String hash) {
        JSONArray json = readJsonFromUrl(OSU_API + GET_BEATMAPS + KEY + "&limit=1&h=" + hash);
        if (json == null || json.length() == 0) {
            return null;
        }
        JSONObject info = json.getJSONObject(0);
        Beatmap beatmap = new Beatmap();
        switch (info.getInt("approved")) {
            case (1):
                beatmap.setRankedStatus(RankedStatus.RANKED);
                break;
            case (2):
                beatmap.setRankedStatus(RankedStatus.APPROVED);
                break;
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

    private static JSONArray readJsonFromUrl(String url) {
        count.incrementAndGet();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL(url).openStream(), Charset.forName("UTF-8")))) {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONArray(sb.toString());
        } catch (IOException e) {
            Bot.log("Error at " + url);
            return null;
        } catch (JSONException e) {
            Bot.log(e.getMessage());
            return null;
        }
    }
}

