package com.edge.agent.core.manager.exception;

public class ChannelManagerException extends Exception{
    public ChannelManagerException() {
        super();
    }

    public ChannelManagerException(String message) {
        super(message);
    }

    public ChannelManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelManagerException(Throwable cause) {
        super(cause);
    }

    protected ChannelManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
