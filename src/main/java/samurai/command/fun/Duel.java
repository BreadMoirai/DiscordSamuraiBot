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
package samurai.command.fun;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.duel.ConnectFour;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("duel")
public class Duel extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (context.getMentionedMembers().size() != 1)
            return new ConnectFour(context.getAuthor().getUser());
        else
            return new ConnectFour(context.getAuthor().getUser(), context.getMentionedMembers().get(0).getUser());
    }
}
