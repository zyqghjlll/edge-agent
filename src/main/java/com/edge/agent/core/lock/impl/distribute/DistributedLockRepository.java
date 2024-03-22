package com.edge.agent.core.lock.impl.distribute;

public interface DistributedLockRepository {

    Boolean setLock(String identifier);

    Boolean releaseLock(String identifier);
}
