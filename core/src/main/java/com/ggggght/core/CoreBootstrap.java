package com.ggggght.core;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoreBootstrap {
    private static Instrumentation instrumentation;
    private static CoreBootstrap coreBootstrap;
    private static AtomicBoolean isBindRef = new AtomicBoolean(false);
    public static ClassLoader classLoader;
    private CoreBootstrap(Instrumentation instrumentation,ClassLoader classLoader) {
        this.instrumentation = instrumentation;
        this.classLoader = classLoader;
    }

    public static CoreBootstrap getInstance() {
        return coreBootstrap;
    }

    public synchronized static CoreBootstrap getInstance(Instrumentation instrumentation,ClassLoader classLoader) throws Throwable {
        if (coreBootstrap != null) {
            return coreBootstrap;
        }

        coreBootstrap = new CoreBootstrap(instrumentation,classLoader);
        if (!isBindRef.compareAndSet(false, true)) {
            throw new IllegalStateException("already bind");
        }
        return coreBootstrap;
    }

    public boolean isBind() {
        return isBindRef.get();
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
