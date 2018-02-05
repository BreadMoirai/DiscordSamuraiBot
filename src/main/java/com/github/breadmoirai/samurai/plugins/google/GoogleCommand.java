/*
 *     Copyright 2017-2018 Ton Ly
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
 */
package com.github.breadmoirai.samurai.plugins.google;

import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Content;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.samurai.plugins.personal.BreadMoiraiSamuraiPlugin;
import com.github.breadmoirai.samurai.util.SearchResult;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleCommand {

    @Command
    public void google(CommandEvent event,
                       @Content String content,
                       GooglePlugin plugin,
                       EventWaiter waiter,
                       BreadMoiraiSamuraiPlugin emotes) {

        if (event.requirePermission(Permission.MESSAGE_MANAGE) || !event.hasContent()) {
            return;
        }
        final JSONObject search = plugin.retrieveSearchResults(content, 1);
        if (search == null) {
            event.reply("An unexpected error occurred.");
            return;
        }
        final JSONObject searchInformation = search.getJSONObject("searchInformation");
        if (Long.parseLong(searchInformation.getString("totalResults")) == 0) {
            if (search.has("spelling") && waiter != null) {
                final String correction = search.getJSONObject("spelling").getString("correctedQuery");
                event.reply("No results found. Did you mean __")
                        .append(correction).append("__?")
                        .onSuccess(message -> {
                            final Emote checkEmote = emotes.getCheckEmote();
                            message.addReaction(checkEmote).queue();
                            final Emote xMarkEmote = emotes.getXMarkEmote();
                            message.addReaction(xMarkEmote).queue(aVoid -> {
                                waiter.waitForReaction()
                                        .onMessages(message.getIdLong())
                                        .withId(checkEmote.getIdLong(), xMarkEmote.getIdLong())
                                        .from(event.getAuthor())
                                        .action(reaction -> {
                                            if (reaction.getReactionEmote().getEmote().equals(checkEmote)) {
                                                message.delete().queue();
                                                new GoogleCommand().google(event, content, plugin, null, emotes);
                                            }
                                        });
                            });
                        });
            } else {
                event.reply("No results found.");
                return;
            }
        }
        JSONArray items = search.getJSONArray("items");
        final JSONObject jsonObject = items.getJSONObject(0);
        final SearchResult result = SearchResult.fromGoogle(jsonObject);
        event.replyFormat("**%s**%n__%s__%n%s", result.getTitle(), result.getUrl(), result.getContent());
    }

}
