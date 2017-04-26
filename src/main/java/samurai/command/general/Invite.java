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

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.List;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("invite")
public class Invite extends Command {

    private static final String INVITE_URL = "https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot&permissions=126016";

    @Override
    public SamuraiMessage execute(CommandContext context) {
        final List<String> args = context.getArgs();
        if (args.size() == 1 && (args.get(0).equalsIgnoreCase("plain") || args.get(0).equalsIgnoreCase("noperm"))) {
            return FixedMessage.build(INVITE_URL.substring(0, 78));
        }
        return FixedMessage.build(INVITE_URL);
    }
}
