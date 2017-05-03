/*    Copyright 2017 Ton Ly

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.command.general;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONArray;
import org.json.JSONObject;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.impl.util.Prompt;
import samurai.util.GoogleAPI;
import samurai.util.SearchResult;

@Key({"google", "g"})
public class Google extends Command {
    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        if (!context.hasContent()) return null;
        final JSONObject search = GoogleAPI.retrieveSearchResults(context.getContent(), 1);
        if (search == null) return null;
        final JSONObject searchInformation = search.getJSONObject("searchInformation");
        if (Long.parseLong(searchInformation.getString("totalResults")) == 0) {
            if (search.has("spelling")) {
                final String correction = search.getJSONObject("spelling").getString("correctedQuery");
                return new Prompt(new MessageBuilder().append("No results found. Did you mean __").append(correction).append("__?").build(), prompt -> {
                    new Google().execute(context.clone(context.getKey(), correction)).replace(prompt.getManager(), prompt.getMessage());
                    prompt.getMessage().clearReactions().queue();
                }, prompt -> {
                    prompt.getMessage().clearReactions().queue();
                    prompt.getMessage().editMessage("No results found.").queue();
                });
            } else return FixedMessage.build("No results found.");
        }
        JSONArray items = search.getJSONArray("items");
        final JSONObject jsonObject = items.getJSONObject(0);
        final SearchResult result = SearchResult.fromGoogle(jsonObject);
        return FixedMessage.build(String.format("**%s**\n__%s__\n%s", result.getTitle(), result.getUrl(), result.getContent()));
    }
}
