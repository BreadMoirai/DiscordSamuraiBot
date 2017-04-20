package samurai.audio;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4/15/2017
 */
public class YoutubeAPI {
    private static final AtomicInteger calls;
    private static final String key;

    private static YouTube youtube;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    static {
        calls = new AtomicInteger(0);
        key = ConfigFactory.load().getString("api.youtube");

        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
        }).setApplicationName("DiscordSamuraiBot").build();
        System.out.println("YouTube Initialized");
    }

    public static List<String> getRelated(String videoID, long size) {
        try {
            YouTube.Search.List search = youtube.search().list("id");


            search.setKey(key);
            search.setRelatedToVideoId(videoID);
            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            //search.setPart("id");
            search.setMaxResults(size);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();
            return searchResultList.stream().map(SearchResult::getId).map(ResourceId::getVideoId).map(s -> "https://www.youtube.com/watch?v=" + s).collect(Collectors.toList());
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return Collections.emptyList();
    }


}
