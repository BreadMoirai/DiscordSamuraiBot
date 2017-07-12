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

package samurai;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import samurai7.core.MessageHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ListenerRegisterDeferrer {

    private final MessageHandler messageHandler;
    private Queue<RegisterAction> observerQueue = new ConcurrentLinkedQueue<>();

    public ListenerRegisterDeferrer(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @SubscribeEvent
    public void onEventCycle(Event event) {
        while (!observerQueue.isEmpty()) {
            observerQueue.poll().run();
        }
    }


    private class RegisterAction implements Runnable {

        private Object observer;

        private boolean add;

        public RegisterAction(Object observer, boolean add) {
            this.observer = observer;
            this.add = add;
        }

        @Override
        public void run() {
//            if (add) messageHandler.addListener(observer);
//            else messageHandler.removeListener(observer);
        }
    }
}
