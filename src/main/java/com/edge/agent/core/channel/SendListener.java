package com.edge.agent.core.channel;

/**
 * 管道发送数据监听
 *
 * @author zyq
 */
public interface SendListener<D extends Object> {
    /**
     * 下发成功处理接口
     *
     * @param contact
     * @param data
     */
    void succeed(Contact contact, D data);

    /**
     * 下发失败处理接口
     *
     * @param contact
     * @param data
     * @param exception
     */
    void failed(Contact contact, D data, Exception exception);
}
