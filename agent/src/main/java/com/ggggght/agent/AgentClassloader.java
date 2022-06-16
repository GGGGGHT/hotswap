package com.ggggght.agent;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

public class AgentClassloader extends URLClassLoader {
    public AgentClassloader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

    @Override protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (Objects.nonNull(loadedClass)) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }

        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }

            return aClass;
        } catch (Exception e) {
            // ignore
        }

        return super.loadClass(name, resolve);
    }

    public Class<?> defineClass(String key, byte[] value) {
       return super.defineClass(key,value,0,value.length);
    }
}
