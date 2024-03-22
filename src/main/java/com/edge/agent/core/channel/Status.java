package com.edge.agent.core.channel;

import lombok.Getter;

/**
 * @author zyq
 */

@Getter
public enum Status {
    /**
     * 未连接
     */
    NOT_CONNECTION("NOT_CONNECTION", "未连接"),
    /**
     * 连接中
     */
    CONNECTING("CONNECTING", "连接中（首次或重连）"),
    /**
     * 连接成功
     */
    CONNECTION_SUCCEEDED("CONNECTION_SUCCEEDED", "连接成功"),
    /**
     * 连接失败
     */
    CONNECTION_FAILED("CONNECTION_FAILED", "连接失败"),
    /**
     * 通信正常
     */
    COMMUNICATION_SUCCEEDED("COMMUNICATION_SUCCEEDED", "通信正常"),
    /**
     * 通信失败
     */
    COMMUNICATION_FAILED("COMMUNICATION_FAILED", "通信失败"),
    ;
    private String code;
    private String message;

    Status(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
