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

package samurai7.modules;

import samurai7.core.Command;
import samurai7.core.IModule;
import samurai7.core.impl.CommandProcessorConfiguration;

public class EmptyModule implements IModule {
    @Override
    public void init(CommandProcessorConfiguration config) {

    }

    @Override
    public Command getCommand(String key) {
        return null;
    }


}
