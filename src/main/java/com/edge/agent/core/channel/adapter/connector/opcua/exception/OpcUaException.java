package com.edge.agent.core.channel.adapter.connector.opcua.exception;

public class OpcUaException extends Exception{
    public OpcUaException() {
        super();
    }

    public OpcUaException(String message) {
        super(message);
    }

    public OpcUaException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpcUaException(Throwable cause) {
        super(cause);
    }

    protected OpcUaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
