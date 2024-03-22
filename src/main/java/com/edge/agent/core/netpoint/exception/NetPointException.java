package com.edge.agent.core.netpoint.exception;

public class NetPointException extends Exception{
    public NetPointException() {
        super();
    }

    public NetPointException(String message) {
        super(message);
    }

    public NetPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetPointException(Throwable cause) {
        super(cause);
    }

    protected NetPointException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
