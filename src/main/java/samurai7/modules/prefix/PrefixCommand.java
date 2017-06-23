/*
 *       Copyright 2017 Ton Ly
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
package samurai7.modules.prefix;

import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai7.core.Command;
import samurai7.core.ICommandEvent;
import samurai7.core.response.Response;
import samurai7.util.DiscordPatterns;

@Admin
@Key("prefix")
public class PrefixCommand extends Command<PrefixModule> {
    @Override
    public Response execute(ICommandEvent event, PrefixModule module) {
        if (!event.hasContent())
            return Response.of("The current prefix is `" + event.getPrefix() + "`");
        final String content = event.getContent().trim().toLowerCase();
        if (content.length() > 16) {
            return Response.of("New prefix must be less than 16 characters.");
        } else if (DiscordPatterns.WHITE_SPACE.matcher(content).find()) {
            return Response.of("New prefix must not contain spaces");
        } else {
            module.changePrefix(event.getGuildId(), content);
            return Response.of("Prefix set to `" + content + "`");
        }
    }
}
