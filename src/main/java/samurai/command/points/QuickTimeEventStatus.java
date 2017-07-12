/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
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
package samurai.command.points;

import samurai.Bot;
import samurai.SamuraiDiscord;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;
import samurai.util.TimeUtil;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Key("qte")
public class QuickTimeEventStatus extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Object o = Bot.getShards().get(0).getRegisteredListeners().get(0);
        final SamuraiDiscord sd = o instanceof SamuraiDiscord ? ((SamuraiDiscord) o) : null;
        if (sd == null) return null;
        final Duration coolDown = sd.getQuickTimeEventController().getCoolDown();
        if (coolDown.equals(Duration.ZERO)) return FixedMessage.build("A QTE is already in progress");
        else return FixedMessage.build("Next QTE will be available in " + TimeUtil.format(coolDown, ChronoUnit.MINUTES));
    }
}
