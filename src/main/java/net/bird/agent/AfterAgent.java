/*
 *    Copyright 2021 Binglong Li
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.bird.agent;

import net.bird.advice.MyAdvice;
import net.bird.advice.MyRunnableWrapper;
import net.bird.listener.TransformListener;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2021/1/4
 */
public class AfterAgent {


    public static void premain(String agentArgs, Instrumentation inst) throws IOException, UnmodifiableClassException {

        File temp = Files.createTempDirectory("tmp")
                .toFile();
        Map<TypeDescription, byte[]> bootstrapClassMap = new HashMap<>();

        TypeDescription type = new TypeDescription.ForLoadedType(MyRunnableWrapper.class);
        byte[] bytes = ClassFileLocator.ForClassLoader.read(MyRunnableWrapper.class);
        bootstrapClassMap.put(type, bytes);
        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst)
                .inject(bootstrapClassMap);
        new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .type(target -> target.getName().equals("java.util.concurrent.ThreadPoolExecutor"))
                .transform((builder, typeDescription, classLoader, module) -> builder
                        .visit(Advice.to(MyAdvice.class).on(target -> target.getName().equals("execute"))))
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .with(RedefinitionStrategy.Listener.ErrorEscalating.FAIL_FAST)
                .with(new TransformListener())
                .installOn(inst);

    }

}
