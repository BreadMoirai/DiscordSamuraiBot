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
package com.github.breadmoirai.samurai.command.restricted;

import com.github.breadmoirai.samurai.Bot;
import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Creator;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.command.annotations.Source;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.9 - 2/16/2017
 */
@Key("close")
@Source
@Creator
public class Shutdown extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        Bot.shutdown();
        return null;
    }
}
