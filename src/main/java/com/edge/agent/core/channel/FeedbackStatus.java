package com.edge.agent.core.channel;

import lombok.Getter;

@Getter
public enum FeedbackStatus {
    /**
     * 未开始
     */
    FEEDBACK_UNSTART("FEEDBACK_UNSTART", "未开始"),
    /**
     * 等待回调
     */
    FEEDBACK_PROCESSING("FEEDBACK_PROCESSING", "等待回调"),
    /**
     * 回调成功
     */
    FEEDBACK_SUCCEEDED("FEEDBACK_SUCCEEDED", "回调成功"),
    /**
     * 等待超时
     */
    FEEDBACK_TIMEOUT("FEEDBACK_TIMEOUT", "等待超时"),
    /**
     * 回调异常
     */
    FEEDBACK_EXCEPTION("FEEDBACK_EXCEPTION", "回调异常"),
    ;
    private String code;
    private String message;

    FeedbackStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
