/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.music;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class YoutubeAPI extends YoutubeSearchProvider{
    private static int calls;
    private static String key;

    private static YouTube youtube;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * @param sourceManager YouTube source manager used for created tracks.
     */
    public YoutubeAPI(YoutubeAudioSourceManager sourceManager) {
        super(sourceManager);
    }

    public static void load(String apiKey, String applicationName) {
        if (apiKey == null) return;
        calls = 0;
        key = apiKey;
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
        }).setApplicationName(applicationName).build();
        System.out.println("YouTube Initialized");
    }

    static boolean isEnabled() {
        return key != null;
    }

    public static List<String> getRelated(String videoID, long size) {
        if (key == null) return Collections.emptyList();
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

            // Call the API.
            SearchListResponse searchResponse = search.execute();
            //System.out.println(searchResponse.toPrettyString());
            calls++;
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
