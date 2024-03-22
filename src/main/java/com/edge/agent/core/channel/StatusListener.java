package com.edge.agent.core.channel;

import java.util.Date;

/**
 * @author zyq
 */
public interface StatusListener {
    /**
     * 管道状态监听处理接口
     * @param contact
     * @param originalState
     * @param currentStatus
     * @param changedDate
     */
    void statusChanged(Contact contact, Status originalState, Status currentStatus, Date changedDate);
}
