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
package samurai.command.osu;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.json.JSONObject;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.api.OsuAPI;

import java.util.List;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("link")
public class Link extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (context.getArgs().size() == 0 || context.getArgs().get(0).length() > 16) {
            return FixedMessage.build("Invalid Username");
        }
        JSONObject userJSON = OsuAPI.getUserJSON(context.getArgs().get(0));
        if (userJSON == null) {
            return FixedMessage.build("Failed to link account.");
        }
        MessageEmbed profileEmbed;
        System.out.println(userJSON.toString());
        List<Member> mentions = context.getMentionedMembers();
        if (context.getMentionedMembers().size() == 0) {
            context.getSamuraiGuild().getManager().addPlayer(
                    context.getAuthorId(),
                    userJSON.getString("username"),
                    userJSON.getInt("user_id"),
                    userJSON.getDouble("pp_raw"),
                    userJSON.getInt("pp_rank"),
                    userJSON.getInt("pp_country_rank"));
            profileEmbed = Profile.buildProfileEmbed(userJSON);
        } else if (context.getMentionedMembers().size() == 1) {
            if (!PermissionUtil.canInteract(context.getAuthor(), context.getMentionedMembers().get(0)))
                return FixedMessage.build("You do not have sufficient access to manage " + context.getMentionedMembers().get(0).getAsMention());
            //todo add user to guild as player
            profileEmbed = Profile.buildProfileEmbed(userJSON);
        } else {
            return FixedMessage.build("Failed to link account.");
        }
        return new FixedMessage().setMessage(new MessageBuilder()
                .append("Successfully linked **")
                .append(mentions.size() == 1 ? mentions.get(0).getAsMention() : context.getAuthor().getAsMention())
                .append("** to osu account")
                .setEmbed(profileEmbed)
                .build());


    }
}
