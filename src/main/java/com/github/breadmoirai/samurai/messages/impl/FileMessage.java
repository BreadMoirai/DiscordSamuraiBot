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
package com.github.breadmoirai.samurai.messages.impl;

import com.github.breadmoirai.samurai.messages.MessageManager;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import net.dv8tion.jda.core.entities.Message;

import java.io.InputStream;

/**
 * @author TonTL
 * @version 4.x - 3/11/2017
 */
public class FileMessage extends SamuraiMessage {

    private long channelId;
    private InputStream data;
    private Message message;
    private String fileName;

    public FileMessage(long channelId, InputStream data, String fileName, Message message) {
        this.channelId = channelId;
        this.data = data;
        this.fileName = fileName;
        this.message = message;
    }

    @Override
    public void send(MessageManager messageManager) {
        messageManager.getClient().getTextChannelById(String.valueOf(channelId)).sendFile(data, fileName, message).queue();
    }

    @Override
    protected Message initialize() {
        return null;
    }

    @Override
    protected void onReady(Message message) {

    }
}
