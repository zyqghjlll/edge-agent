package com.edge.agent.core.lock;

/**
 * @author zyq
 */
public interface Lockable {
    void withLock(Function function);

    void withLock(Function function, Recorder recorder);

}
