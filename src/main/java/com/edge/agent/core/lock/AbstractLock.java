package com.edge.agent.core.lock;

import com.edge.agent.core.lock.exception.BreakLockException;
import com.edge.agent.core.lock.exception.LockException;
import com.edge.agent.core.lock.exception.UnLockException;

/**
 * @author zyq
 */
public abstract class AbstractLock implements Lockable {
    private String identifier;
    private Status status;
    private Recorder recorder;

    public AbstractLock() {
    }

    public boolean isLocked() {
        if (this.status.equals(Status.LOCKED)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isUnLocked() {
        if (this.status.equals(Status.UNLOCKED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * lock the resource
     *
     * @return
     * @throws LockException
     */
    public abstract boolean lock() throws LockException;

    /**
     * unlock the resource
     *
     * @throws UnLockException
     */
    public abstract void unlock() throws UnLockException;

    /**
     * How to handle a failed unlock situation.
     *
     * @throws BreakLockException
     */
    public abstract void breakStrategy() throws BreakLockException;

    @Override
    public void withLock(Function function) {
        withLock(function, null);
    }

    @Override
    public void withLock(Function function, Recorder recorder) {
        try {
            this.recorder = recorder;
            lock();
            this.status = Status.LOCKED;
            this.record("Locked successfully");
        } catch (LockException e) {
            this.record("An error occurred during lock", e);
        }
        try {
            function.execute();
        } finally {
            try {
                unlock();
                this.status = Status.UNLOCKED;
                this.record("Unlocked successfully");
            } catch (UnLockException e) {
                this.record("An error occurred during unlock", e);
                try {
                    breakStrategy();
                    this.record("The lock broke successfully");
                } catch (BreakLockException ex) {
                    this.record("An error occurred during break lock", e);
                }
            }
        }
    }

    private void record(String message, LockException exception) {
        if (this.recorder != null) {
            String prefix = "[Locker:" + identifier + "]-";
            this.recorder.record(prefix + message, exception);
        }
    }

    private void record(String message) {
        this.record(message, null);
    }
}
