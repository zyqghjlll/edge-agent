package com.edge.agent.core.lock.repository;

import cn.hutool.core.collection.CollUtil;
import com.edge.agent.core.lock.impl.distribute.DistributedLockRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * @author zyq
 */
@Repository
public class MockDistributedLockRepositoryImpl implements DistributedLockRepository {

    private HashMap<String, String> lockMap = new HashMap<>();

    @Override
    public Boolean setLock(String identifier) {
        while (true) {
            if (lock(identifier)) {
                return true;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Boolean releaseLock(String identifier) {
        boolean result = false;
        if (unlock(identifier)) {
            result = true;
        }

        return result;
    }

    private synchronized boolean lock(String identifier) {
        if (CollUtil.isEmpty(this.lockMap)) {
            this.lockMap.put(identifier, "lock");
            return true;
        } else {
            return false;
        }
    }

    private synchronized boolean unlock(String identifier) {
        if (CollUtil.isNotEmpty(this.lockMap) && this.lockMap.containsKey(identifier)) {
            this.lockMap.remove(identifier);
            return true;
        } else {
            return false;
        }
    }
}
