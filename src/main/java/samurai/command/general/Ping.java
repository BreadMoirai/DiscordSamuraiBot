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

import static java.time.temporal.ChronoUnit.MILLIS;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
@Key("ping")
public class Ping extends Command {

    public SamuraiMessage execute(CommandContext context) {
        return FixedMessage.build("Calculating Ping...").setConsumer(message -> message.editMessage(String.format("Pung! %dms", MILLIS.between(context.getTime(), message.getCreationTime()))).queue());
    }
}