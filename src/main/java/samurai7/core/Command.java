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

package samurai7.core;

import samurai7.core.response.Response;

import java.util.Map;
import java.util.Optional;

public abstract class Command<M extends IModule> {

    private M module;
    private ICommandEvent event;

    public Optional<Response> call() {
        Optional<Response> messageOptional = Optional.ofNullable(execute(event, module));
        return messageOptional;
    }

    protected abstract Response execute(ICommandEvent event, M module);

    public ICommandEvent getEvent() {
        return event;
    }

    public final void setModule(Map<Class<? extends IModule>, IModule> moduleMap) {
        try {
            final String module = this.getClass().getMethod("execute", ICommandEvent.class, Object.class).getParameterTypes()[1].getSimpleName();
            System.out.println("module = " + module);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();

        }
    }
}
