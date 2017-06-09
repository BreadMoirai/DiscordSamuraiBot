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
package samurai.command.items;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.command.manage.Schedule;
import samurai.items.Item;
import samurai.items.ItemFactory;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.RedPacketDrop;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Creator
@Key("/rpd")
public class RedPacket extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<String> args = context.getArgs();
        final Duration duration = Schedule.getDuration(args);
        final int size = args.size();
        final int[] drops = new int[size];
        final ArrayList<Integer> dropQueueList = new ArrayList<>();
        for (int i = 0; i < size - 1; i += 2) {
            final String itemId = args.get(i);
            final String count = args.get(i + 1);
            if (!CommandContext.isNumber(itemId) || !CommandContext.isNumber(count)) return null;
            final int count1 = Integer.parseInt(count);
            if (count1 == 0) continue;
            else if (count1 < 0) return null;
            final int itemId1 = Integer.parseInt(itemId);
            final Item item = ItemFactory.getItemById(itemId1);
            if (item == null) return null;
            drops[i] = itemId1;
            drops[i + 1] = count1;
            for (int j = 0; j < count1; j++) {
                dropQueueList.add(itemId1);
            }
        }
        Collections.shuffle(dropQueueList);
        final int[] dropQueue = dropQueueList.stream().mapToInt(Integer::intValue).toArray();
        return new RedPacketDrop(duration, drops, dropQueue);
    }
}
