package dreadmoirais.samurais;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static final String PROFILE_URL = "https://osu.ppy.sh/api/get_user?k=59258eb34b84d912c79cf1ecb7fc285b79e16194&type=string&u=";

    OsuJsonReader() {

    }

    public static Message getUserInfo(String name) {
        List<JSONObject> json;
        try {
            json = readJsonFromUrl(PROFILE_URL+name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (json.isEmpty()) {
            return null;
        }
        JSONObject profile = json.get(0);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(profile.getString("username"))
                .setColor(Color.PINK)
                .addField("Level", profile.getString("level"), true)
                .addField("Play Count", profile.getString("playcount"), true)
                .addField("Rank", profile.getString("pp_rank"), true)
                .addField("Accuracy", profile.getString("accuracy"), true)
                .addField("SS Count", profile.getString("count_rank_ss"), true)
                .addField("S Count", profile.getString("count_rank_s"), true)
                .addField("A Count", profile.getString("count_rank_a"), true)
                .setFooter("Osu!API", "http://w.ppy.sh/c/c9/Logo.png");
        return new MessageBuilder().setEmbed(eb.build()).build();

    }

    private static List<String> readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        String text = sb.toString();
        text = text.substring(1,text.length()-1);
        List<String> jsonStrings = new ArrayList<>();
        int i = -1;
        while ((i=text.indexOf('{',i)) != -1) {
            int j = text.indexOf("]}")+2;
            jsonStrings.add(text.substring(i,j));
            i=j;
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
