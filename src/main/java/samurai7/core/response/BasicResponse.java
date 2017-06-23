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

package samurai7.core.response;

import net.dv8tion.jda.core.entities.Message;

import java.util.function.Consumer;

public class BasicResponse extends Response {

    private Message message;
    private Consumer<Message> consumer;

    public BasicResponse(Message message) {
        this.message = message;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public void onSend(Message message) {
        if (consumer != null) consumer.accept(message);
    }

    public BasicResponse andThen(Consumer<Message> onSend) {
        if (consumer == null) consumer = onSend;
        else consumer = consumer.andThen(onSend);
        return this;
    }
}
