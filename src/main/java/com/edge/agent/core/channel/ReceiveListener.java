package com.edge.agent.core.channel;

/**
 * 管道获取数据监听
 * @author zyq
 */

public interface ReceiveListener<U extends Object> {
    /**
     * 接收数据监听处理接口
     * @param contact
     * @param data
     */
    void handle(Contact contact, U data);
}
