/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.osu;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import samurai.command.music.Play;
import samurai.database.objects.PlayerBuilder;
import samurai.osu.model.Score;
import samurai.osu.enums.GameMode;
import samurai.osu.enums.Grade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OsuAPI {

    private static final String OSU_API;
    private static final String KEY;
    private static final String GET_USER;
    private static final String GET_BEATMAPS;
    private static final String GET_SCORES;
    private static final String GET_RECENT;
    public static int calls;

    static {
        calls = 0;
        OSU_API = "https://osu.ppy.sh/api/";
        final Config load = ConfigFactory.load();
        if (load.hasPath("api.osu")) {
            KEY = "k=" + load.getString("api.osu");
        } else {
            System.err.println("No Osu Api Key found");
            KEY = null;
        }
        GET_USER = "get_user?";
        GET_BEATMAPS = "get_beatmaps?";
        GET_SCORES = "get_scores?";
        GET_RECENT = "get_user_recent?";
    }

    public static boolean isEnabled() {
        return KEY != null;
    }

    public static List<Score> getUserRecent(String player, int userId, GameMode m, int limit) {
        if (KEY == null) return Collections.emptyList();
        String url = String.format("%s%s%s&u=%d&m=%d&limit=%d&type=id", OSU_API, GET_RECENT, KEY, userId, m.value(), limit);
        final JSONArray jsonArray = readJsonFromUrl(url);
        if (jsonArray == null) return Collections.emptyList();
        return IntStream.range(0, limit).mapToObj(jsonArray::optJSONObject).filter(Objects::nonNull).map(jsonObject -> scoreFromJson(jsonObject, player)).peek(score -> score.setPlayerId(userId)).collect(Collectors.toList());
    }

    private static Score scoreFromJson(JSONObject jsonObject, String player) {
        Score s = new Score()
                .setPlayer(player);
        final int beatmap_id = jsonObject.getInt("beatmap_id");
        s
                .setBeatmapId(jsonObject.getInt("beatmap_id"))
                .setScore(jsonObject.getInt("score"))
                .setMaxCombo((short) jsonObject.getInt("maxcombo"))
                .setCount0((short) jsonObject.getInt("countmiss"))
                .setCount50((short) jsonObject.getInt("count50"))
                .setCount100((short) jsonObject.getInt("count100"))
                .setCount300((short) jsonObject.getInt("count300"))
                .setKatu((short) jsonObject.getInt("countkatu"))
                .setGeki((short) jsonObject.getInt("countgeki"))
                .setPerfectCombo(jsonObject.getInt("perfect") == 1)
                .setModCombo(jsonObject.getInt("enabled_mods"))
                .setGrade(Grade.valueOf(jsonObject.getString("rank")))
                .setTimestamp(OffsetDateTime.parse(jsonObject.getString("date").replace(' ', 'T') + "+08:00").toEpochSecond());
        return s;
    }

    public static PlayerBuilder getPlayer(int osuId) {
        if (KEY == null) return null;
        JSONArray json = readJsonFromUrl(String.format("%s%s%s&type=id&u=%d", OSU_API, GET_USER, KEY, osuId));
        if (json == null || json.length() != 1) return null;
        return playerFromJson(json.getJSONObject(0));
    }

    public static PlayerBuilder getPlayer(String osuName) {
        if (KEY == null) return null;
        JSONArray json = readJsonFromUrl(String.format("%s%s%s&u=%s", OSU_API, GET_USER, KEY, osuName));
        if (json == null || json.length() != 1) return null;
        return playerFromJson(json.getJSONObject(0));
    }

    public static PlayerBuilder playerFromJson(JSONObject userJSON) {
        PlayerBuilder p = new PlayerBuilder();
        p.setOsuName(userJSON.getString("username"));
        p.setOsuId(userJSON.getInt("user_id"));
        p.setGlobalRank(userJSON.getInt("pp_rank"));
        p.setCountryRank(userJSON.getInt("pp_country_rank"));
        p.setLevel(userJSON.getDouble("level"));
        p.setRawPP(userJSON.getDouble("pp_raw"));
        p.setAccuracy(userJSON.getDouble("accuracy"));
        p.setCountX(userJSON.getInt("count_rank_ss"));
        p.setCountS(userJSON.getInt("count_rank_s"));
        p.setCountA(userJSON.getInt("count_rank_a"));
        p.setPlayCount(userJSON.getInt("playcount"));
        p.setLastUpdated(Instant.now().getEpochSecond());
        return p;
    }

    public static JSONArray getSetByHash(String hash) {
        if (KEY == null) return null;
        JSONArray jsonArray = readJsonFromUrl(String.format("%s%s%s&h=%s", OSU_API, GET_BEATMAPS, KEY, hash));
        if (jsonArray != null) {
            return getSet(jsonArray.getJSONObject(0).getInt("beatmapset_id"));
        }
        return null;
    }

    public static JSONArray getSetByMap(int mapId) {
        if (KEY == null) return null;
        JSONArray jsonArray = readJsonFromUrl(String.format("%s%s%s&b=%d", OSU_API, GET_BEATMAPS, KEY, mapId));
        if (jsonArray != null) {
            return getSet(jsonArray.getJSONObject(0).getInt("beatmapset_id"));
        }
        return null;
    }

    public static JSONArray getSet(int setId) {
        if (KEY == null) return null;
        return readJsonFromUrl(String.format("%s%s%s&s=%d", OSU_API, GET_BEATMAPS, KEY, setId));
    }

    private static JSONArray readJsonFromUrl(String url) {
        calls++;
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            //System.out.println(sb.toString());
            JSONArray jsonArray = new JSONArray(sb.toString());
            if (jsonArray.length() == 0) return null;
            return jsonArray;
        } catch (IOException | JSONException e) {
            //todo log error
            return null;
        }
    }
}

