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
package samurai.messages.impl;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import samurai.messages.base.SamuraiMessage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PermissionFailureMessage extends SamuraiMessage {

    private final Member selfMember;
    private final Channel channel;
    private final Permission[] permissionsRequired;

    public PermissionFailureMessage(Member selfMember, Channel channel, Permission... permissions) {
        this.selfMember = selfMember;
        this.channel = channel;
        this.permissionsRequired = permissions;
    }

    @Override
    protected Message initialize() {
        final MessageBuilder messageBuilder = new MessageBuilder();
        final List<Permission> permissionsFound = selfMember.getPermissions(channel);
        messageBuilder.append("This command requires additional permissions");
        if (channel.getIdLong() != getChannelId()) {
            messageBuilder.append(" in channel ");
            if (channel.getType() == ChannelType.TEXT)
                messageBuilder.append(((TextChannel) channel).getAsMention());
            else messageBuilder.append("**").append(channel.getName()).append("**");
        }
        messageBuilder.append(" to execute\n```diff");
        Arrays.stream(permissionsRequired).map(permission -> (permissionsFound.contains(permission) ? "\n+ " : "\n- ") + permission.getName()).sorted(Comparator.comparingInt(s -> s.codePointAt(0))).forEachOrdered(messageBuilder::append);
        messageBuilder.append("\n```");
        return messageBuilder.build();
    }

    @Override
    protected void onReady(Message message) {

    }
}
