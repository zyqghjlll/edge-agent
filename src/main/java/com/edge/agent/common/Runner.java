package com.edge.agent.common;

import com.edge.agent.utils.SysLogger;

public abstract class Runner implements Runnable {
    private volatile boolean isPaused;
    private volatile boolean isStopped = false;
    private volatile long millisecond;

    /**
     * 初始化
     *
     * @param isPaused 是否暂停状态 true:暂停状态；false:启动状态
     */
    protected Runner(boolean isPaused) {
        this(isPaused, 50);
    }

    /**
     * 初始化
     *
     * @param isPaused    是否暂停状态 true:暂停状态；false:启动状态
     * @param millisecond 休眠毫秒数
     */
    protected Runner(boolean isPaused, long millisecond) {
        this.isPaused = isPaused;
        this.millisecond = millisecond;
    }

    @Override
    public void run() {
        while (!isStopped) {
            synchronized (this) {
                while (isPaused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // 处理线程中断异常
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // 休眠
            try {
                // 执行线程的逻辑代码
                execute();
            } catch (Exception e) {
                SysLogger.error(e, "Runner运行时异常");
            } finally {
                try {
                    //SysLogger.debug(Thread.currentThread().getName() + "休眠 " + millisecond + "ms");
                    Thread.sleep(millisecond);
                } catch (InterruptedException e) {
                    SysLogger.error(e, "Thread.sleep error.");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public abstract void execute();

    /**
     * 是否暂停
     *
     * @return
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 暂停
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * 恢复
     */
    public synchronized void resume() {
        isPaused = false;
        notifyAll();
    }

    /**
     * 停止
     */
    public synchronized void stop() {
        isStopped = true;
        notifyAll();
    }
}
