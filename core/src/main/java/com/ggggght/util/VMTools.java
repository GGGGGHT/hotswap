package com.ggggght.util;

public class VMTools {
    private static VMTools instance = new VMTools();


    public VMTools() {
    }


    public static <T> T[] getInstances(Class<T> klass) {
        return getInstances0(klass);
    }

    /**
     * 获取某个class在jvm中当前所有存活实例
     */
    private static synchronized native <T> T[] getInstances0(Class<T> klass);
}
