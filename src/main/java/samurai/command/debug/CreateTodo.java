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
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.TodoMessageList;

import java.util.Arrays;

@Creator
@Key("/createtodo")
public class CreateTodo extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        context.getChannel().deleteMessageById(context.getMessageId()).queue();
        return new TodoMessageList(Arrays.asList("Bug", "Enhancement", "Feature"));
    }
}
