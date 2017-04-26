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
package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.annotations.MessageScope;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.test.UniqueTestMessage;

/**
 * @author TonTL
 * @version 4/19/2017
 */
@Key("unique")
public class UniqueTest extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        switch (context.getContent().toLowerCase()) {
            case "author":
                return new UniqueTestMessage(MessageScope.Author);
            case "channel":
                return new UniqueTestMessage(MessageScope.Channel);
            case "guild":
                return new UniqueTestMessage(MessageScope.Guild);
            default:
                return null;
        }
    }
}
