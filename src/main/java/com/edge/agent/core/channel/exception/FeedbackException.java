package com.edge.agent.core.channel.exception;

public class FeedbackException extends Exception{
    public FeedbackException() {
        super();
    }

    public FeedbackException(String message) {
        super(message);
    }

    public FeedbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedbackException(Throwable cause) {
        super(cause);
    }

    protected FeedbackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
