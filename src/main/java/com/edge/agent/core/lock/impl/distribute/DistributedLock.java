package com.edge.agent.core.lock.impl.distribute;

import com.edge.agent.core.lock.exception.BreakLockException;
import com.edge.agent.core.lock.exception.LockException;
import com.edge.agent.core.lock.exception.UnLockException;
import com.edge.agent.core.lock.AbstractLock;

/**
 * @author zyq
 */
public class DistributedLock extends AbstractLock {
    private String identifier;
    private DistributedLockRepository distributedLockRepository;

    public DistributedLock(String identifier, DistributedLockRepository distributedLockRepository) {
        this.identifier = identifier;
        this.distributedLockRepository = distributedLockRepository;
    }

    @Override
    public boolean lock() throws LockException {
        boolean result = distributedLockRepository.setLock(this.identifier);
        return result;
    }

    @Override
    public void unlock() throws UnLockException {
        boolean result = distributedLockRepository.releaseLock(this.identifier);
    }

    @Override
    public void breakStrategy() throws BreakLockException {

    }
}
