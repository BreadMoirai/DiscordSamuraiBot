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

package samurai.database;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;
import samurai7.core.Command;
import samurai7.core.ICommandEvent;
import samurai7.core.IModule;
import samurai7.core.response.Response;
import samurai7.modules.EmptyModule;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

public class CommandTest {


    @Test
    public void testCommandModule() {
        try {
            final Method[] methods = TestCommand.class.getMethods();
            System.out.println("methods = " + Arrays.toString(methods).replace(",", ",\n"));
            final Method[] declaredMethods = TestCommand.class.getDeclaredMethods();
            System.out.println("declaredMethods = " + Arrays.toString(declaredMethods).replace(",", ",\n"));
            final Method execute = TestCommand.class.getDeclaredMethod("execute", ICommandEvent.class, IModule.class);
            System.out.println("execute = " + execute);
            final Type[] genericParameterTypes = execute.getGenericParameterTypes();
            System.out.println("genericParameterTypes = " + Arrays.toString(genericParameterTypes));
            final TypeVariable<Method>[] typeParameters = execute.getTypeParameters();
            System.out.println("typeParameters = " + Arrays.toString(typeParameters));
            final Parameter[] parameters = execute.getParameters();
            System.out.println("parameters = " + Arrays.toString(parameters));
            final Command testCommand = new TestCommand();
            final Type type = testCommand.getModuleType();
            System.out.println("type = " + type);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }






    private class TestCommand extends Command{

        @Override
        public Response execute(ICommandEvent event, IModule module) {
            return null;
        }
    }

}
