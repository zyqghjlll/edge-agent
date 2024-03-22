package com.edge.agent.core.lock;

/**
 * @author zyq
 */
public enum Status {
    /**
     * LOCKED
     */
    LOCKED("LOCKED", "LOCKED"),
    /**
     * UNLOCKED
     */
    UNLOCKED("UNLOCKED", "UNLOCKED"),
    ;
    private String code;
    private String message;

    Status(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
