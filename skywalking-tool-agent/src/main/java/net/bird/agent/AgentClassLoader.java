package net.bird.agent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2021/7/7
 */
public class AgentClassLoader extends URLClassLoader {

    public AgentClassLoader() {
        super(getUrls(), AgentClassLoader.class.getClassLoader());
    }

    /**
     * 在这里打破了双亲委派模型，主要目的是隔离agent日志和应用日志。
     * @param name
     * @param resolve
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                if (name.contains("net.bird.agent")) {
                    c = findClass(name);
                } else {
                    c = super.loadClass(name, resolve);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }


    private static URL[] getUrls(){
        URL agentJar = AgentClassLoader.class.getProtectionDomain()
                .getCodeSource()
                .getLocation();
        URL resource = AgentClassLoader.class.getClassLoader()
                .getResource("net/bird/agent/");
        return new URL[]{agentJar, resource};
    }
}
