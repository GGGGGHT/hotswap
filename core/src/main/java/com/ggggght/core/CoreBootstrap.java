package com.ggggght.core;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoreBootstrap {
    private static Instrumentation instrumentation;
    private static CoreBootstrap coreBootstrap;
    private static AtomicBoolean isBindRef = new AtomicBoolean(false);
    private CoreBootstrap(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public synchronized static CoreBootstrap getInstance(Instrumentation instrumentation, String args) throws Throwable {
        if (coreBootstrap != null) {
            return coreBootstrap;
        }

        coreBootstrap = new CoreBootstrap(instrumentation);
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
