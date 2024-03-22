package com.edge.agent.core.channel.exception;

public class ChannelStatusException extends Exception{
    public ChannelStatusException() {
        super();
    }

    public ChannelStatusException(String message) {
        super(message);
    }

    public ChannelStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelStatusException(Throwable cause) {
        super(cause);
    }

    protected ChannelStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
