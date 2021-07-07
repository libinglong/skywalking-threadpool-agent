package net.bird.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2021/7/7
 */
public class Bootstrap {

    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> aClass = Class.forName("net.bird.agent.Agent", false, new AgentClassLoader());
        Method premain = aClass.getDeclaredMethod("premain", String.class, Instrumentation.class);
        premain.invoke(null, agentArgs, inst);
    }

}
