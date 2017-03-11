package samurai.osu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import samurai.core.Bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    public static JSONArray getBeatmapSetArrayFromMap(String hash) {
        JSONArray jsonArray = readJsonFromUrl(String.format("%s%s%s&h=%s", OSU_API, GET_BEATMAPS, KEY, hash));
        if (jsonArray != null) {
            return getBeatmapSetArray(jsonArray.getJSONObject(0).getInt("beatmapset_id"));
        }
        return null;
    }

    public static JSONArray getBeatmapSetArrayFromMap(int mapId) {
        JSONArray jsonArray = readJsonFromUrl(String.format("%s%s%s&b=%d", OSU_API, GET_BEATMAPS, KEY, mapId));
        if (jsonArray != null) {
            return getBeatmapSetArray(jsonArray.getJSONObject(0).getInt("beatmapset_id"));
        }
        return null;
    }

    public static JSONArray getBeatmapSetArray(int setId) {
        return readJsonFromUrl(String.format("%s%s%s&s=%d", OSU_API, GET_BEATMAPS, KEY, setId));
    }

    private static JSONArray readJsonFromUrl(String url) {
        count.incrementAndGet();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());
            if (jsonArray.length() == 0) return null;
            return jsonArray;
        } catch (IOException e) {
            Bot.log("Error at " + url);
            return null;
        } catch (JSONException e) {
            Bot.log(e.getMessage());
            return null;
        }
    }
}

