package com.edge.agent.core.lock;

import com.edge.agent.core.lock.exception.LockException;

@FunctionalInterface
public interface Recorder {
    /**
     * record information and exceptions, certainly, the exception maybe is null.
     * @param message
     * @param exception
     */
    void record(String message, LockException exception);
}
