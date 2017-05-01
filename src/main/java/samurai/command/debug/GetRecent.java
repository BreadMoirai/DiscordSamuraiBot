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
import samurai.command.annotations.Source;
import samurai.database.Database;
import samurai.entities.model.Player;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.osu.ScoreDisplay;
import samurai.messages.impl.osu.StaticBeatmapDisplay;
import samurai.osu.OsuAPI;
import samurai.osu.enums.GameMode;
import samurai.osu.model.Score;

import java.util.Optional;

@Key("recent")
@Source
public class GetRecent extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<Player> playerOptional = Database.getDatabase().getPlayer(context.getAuthorId());
        if (!playerOptional.isPresent()) {
            return FixedMessage.build("Please link your osu! profile.");
        }
        final Player player = playerOptional.get();
        final Optional<Score> userRecent = OsuAPI.getUserRecent(player.getOsuName(), player.getOsuId(), GameMode.STANDARD, 50).stream().filter(Score::passed).findFirst();
        if (!userRecent.isPresent()) return FixedMessage.build("Nothing found.");
        final Score lastScore = userRecent.get();
        return new ScoreDisplay(lastScore);
    }
}
