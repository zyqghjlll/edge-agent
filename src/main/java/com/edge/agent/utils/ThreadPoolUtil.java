package com.edge.agent.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zyq
 * @description 线程池工具类
 */

public class ThreadPoolUtil {
    private static ExecutorService executor;

    private ThreadPoolUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static ExecutorService initThreadPool(int threadCount) {
        executor = Executors.newFixedThreadPool(threadCount);
        return executor;
    }

    public static void submitTask(Runnable task) {
        executor.submit(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
