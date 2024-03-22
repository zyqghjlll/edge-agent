package com.edge.agent.core.agent;

import lombok.Getter;

@Getter
public enum Status {
    /**
     * 未运行
     */
    NOT_RUNNING("NOT_RUNNING", "未运行"),
    /**
     * 运行中
     */
    RUNNING("RUNNING", "运行中"),
    ;
    private String code;
    private String message;

    Status(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
