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
package samurai.qte;

import samurai.SamuraiDiscord;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;

public abstract class QuickTimeMessage extends DynamicMessage implements Reloadable{

    private static final long serialVersionUID = 10L;

    private transient QuickTimeEventController qte;

    void setQuickTimeEventController(QuickTimeEventController qte) {
        this.qte = qte;
    }

    void fireCompletionEvent(boolean overrideDelay) {
        if (overrideDelay) qte.sendNewQuiz();
        else qte.onCompletion();
    }

    @Override
    public void reload(SamuraiDiscord samuraiDiscord) {
        setQuickTimeEventController(samuraiDiscord.getQuickTimeEventController());
        replace(samuraiDiscord.getMessageManager(), getMessageId());
    }
}
