package com.edge.agent.core.remote;

/**
 * @author zyq
 */
public abstract class Remote {

    abstract void send();

    abstract void report();

    abstract void subscribe();
}
