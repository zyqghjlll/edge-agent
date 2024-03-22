package com.edge.agent.core.channel;

/**
 * @author zyq
 */
public enum ConnectionType {
    /**
     * 长连接
     * 指在一个连接上可以连续发送多个数据包，在连接保持期间，如果没有数据包发送，需要双方发链路检测包。
     */
    LONG_CONNECTION("LONG_CONNECTION", "长连接"),
    /**
     * 短连接
     * 指的是在数据传送过程中，只在需要发送数据时，才去建立一个连接，数据发送完成后，则断开此连接，即每次连接只完成一项业务的发送。
     */
    SHORT_CONNECTION("SHORT_CONNECTION", "短连接"),
    ;
    private String code;
    private String message;

    ConnectionType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
