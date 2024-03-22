package com.edge.agent.core.channel;

/**
 * @author zyq
 */
public enum ConnectorType {
    /**
     * SOCKET
     */
    SOCKET("SOCKET", "SOCKET"),
    /**
     * OPC_UA
     */
    OPC_UA("OPC_UA", "OPC_UA"),
    ;
    private String code;
    private String message;

    ConnectorType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
