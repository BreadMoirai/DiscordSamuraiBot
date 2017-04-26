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
import samurai.command.annotations.Source;
import samurai.files.SamuraiStore;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.util.MyLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/23/2017
 */
@Key("flame")
@Source
public class Flame extends Command {

    private static Random r = new Random();

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        try {
            List<String> flameList = Files.readAllLines(Paths.get(SamuraiStore.class.getResource("./flame.txt").toURI()));
            if (context.getMentionedMembers().isEmpty()) {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", context.getAuthor().getAsMention()));
            } else {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", context.getMentionedMembers().get(0).getAsMention()));
            }
        } catch (IOException | URISyntaxException e) {
            MyLogger.log("File Exception", Level.WARNING, e, context);
            return null;
        }
    }
}
