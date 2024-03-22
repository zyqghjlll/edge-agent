package com.edge.agent.core.lock.impl;

import com.edge.agent.core.lock.exception.BreakLockException;
import com.edge.agent.core.lock.exception.LockException;
import com.edge.agent.core.lock.exception.UnLockException;
import com.edge.agent.core.lock.AbstractLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zyq
 */
public class SyncLock extends AbstractLock {
    private ReentrantLock reentrantLock;

    @Override
    public boolean lock() throws LockException {
        this.reentrantLock = new ReentrantLock();
        // 使用锁
        reentrantLock.lock();
        return true;
    }

    @Override
    public void unlock() throws UnLockException {
        reentrantLock.unlock();
    }

    @Override
    public void breakStrategy() throws BreakLockException {

    }
}
