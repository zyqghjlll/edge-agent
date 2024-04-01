package com.edge.agent.core.messenger;

/**
 * @author zyq
 */
public abstract class Messenger {
    private Long id;
    private String from;
    private String to;

    abstract void encode();

    abstract void decode();
}
