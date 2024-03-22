package com.edge.agent.core.channel;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.utils.SysLogger;

import java.util.Date;

/**
 * @author zyq
 */
class StatusEvent {
    private Contact contact;
    private Status currentStatus;
    private Status beforeChange;
    private Status afterChange;
    /**
     * 连接错误信息
     */
    private String errorMessage;

    private StatusListener statusListener;

    public StatusEvent(Contact contact, Status currentStatus, StatusListener statusListener) {
        this.contact = contact;
        this.currentStatus = currentStatus;
        this.statusListener = statusListener;
    }

    public Status status() {
        return this.currentStatus;
    }

    public String errorMessage() {
        return this.errorMessage;
    }

    /**
     * 连接中（首次或重连）
     *
     * @return
     */
    public StatusEvent connectStarted() {
        beforeChange = status();
        afterChange = Status.CONNECTING;
        errorMessage = "";
        statusChanged(beforeChange, afterChange);
        return this;
    }

    /**
     * 连接成功
     *
     * @return
     */
    public StatusEvent connectSucceed() {
        beforeChange = status();
        afterChange = Status.CONNECTION_SUCCEEDED;
        errorMessage = "";
        statusChanged(beforeChange, afterChange);
        return this;
    }

    /**
     * 连接失败
     *
     * @return
     */
    public StatusEvent connectFailed(String message) {
        beforeChange = status();
        afterChange = Status.CONNECTION_FAILED;
        errorMessage = message;
        statusChanged(beforeChange, afterChange);
        return this;
    }

    /**
     * 断开连接
     *
     * @return
     */
    public StatusEvent disconnected() {
        beforeChange = status();
        afterChange = Status.NOT_CONNECTION;
        errorMessage = "";
        statusChanged(beforeChange, afterChange);
        return this;
    }

    /**
     * 通信成功
     *
     * @return
     */
    public StatusEvent communicationSucceed() {
        beforeChange = status();
        afterChange = Status.COMMUNICATION_SUCCEEDED;
        errorMessage = "";
        statusChanged(beforeChange, afterChange);
        return this;
    }

    /**
     * 通信失败
     *
     * @return
     */
    public StatusEvent communicationFailed(String message) {
        beforeChange = status();
        afterChange = Status.COMMUNICATION_FAILED;
        errorMessage = message;
        statusChanged(beforeChange, afterChange);
        return this;
    }

    private void statusChanged(Status originalState, Status currentState) {
        if (ObjectUtil.isNotEmpty(this.statusListener)) {
            if (!originalState.equals(currentState)) {
                SysLogger.info("【PLC状态变化】 originalState:[%s] -> currentState:[%s] ", originalState, currentState);
                this.currentStatus = currentState;
                this.statusListener.statusChanged(contact, originalState, currentState, new Date());
            }
        }
    }
}
