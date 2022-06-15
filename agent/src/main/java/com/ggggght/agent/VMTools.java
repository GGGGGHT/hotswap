package com.ggggght.agent;

public class VMTools {
    /**
     * 获取某个class在jvm中当前所有存活实例
     */
    private static synchronized native <T> T[] getInstances0(Class<T> klass);
}
