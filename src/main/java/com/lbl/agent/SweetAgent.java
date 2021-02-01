package com.lbl.agent;

import com.lbl.advice.MyAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;

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
public class SweetAgent {


    public static void premain(String agentArgs, Instrumentation inst) throws IOException, UnmodifiableClassException {

        File temp = Files.createTempDirectory("tmp")
                .toFile();
        Map<TypeDescription, byte[]> bootstrapClassMap = new HashMap<>();

        TypeDescription type = new TypeDescription.ForLoadedType(RunnableWrapper.class);
        byte[] bytes = ClassFileLocator.ForClassLoader.read(RunnableWrapper.class);
        bootstrapClassMap.put(type, bytes);
        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst)
                .inject(bootstrapClassMap);
        new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .type(target -> target.getName().equals("java.util.concurrent.ThreadPoolExecutor"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                        return builder.visit(Advice.to(MyAdvice.class).on(target -> target.getName().equals("execute")));
                    }
                })
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .with(RedefinitionStrategy.Listener.ErrorEscalating.FAIL_FAST)
                .with(new TransformListener())
                .installOn(inst);

    }

    private static class TransformListener implements AgentBuilder.Listener {


        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {
            System.out.println("On Transformation class " + typeDescription.getName() + " loaded:" + loaded);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

    }

}
