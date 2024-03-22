package com.edge.agent.common;

public class Counter {
    private int currentNum;
    private int currentNumMemery;
    private int targetNum;

    public Counter(int startNum, int totalTimes) {
        this.currentNum = startNum;
        this.currentNumMemery = this.currentNum;
        this.targetNum = this.currentNum + totalTimes;
    }

    /**
     * 计数加 1
     *
     * @return
     */
    public Counter add() {
        this.currentNum++;
        return this;
    }

    /**
     * 重置计数
     *
     * @return
     */
    public Counter reset() {
        this.currentNum = this.currentNumMemery;
        return this;
    }

    public boolean finish() {
        boolean result = false;
        if (this.currentNum >= targetNum) {
            result = true;
        }
        return result;
    }
}
