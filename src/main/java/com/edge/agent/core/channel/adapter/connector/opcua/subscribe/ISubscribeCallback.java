package com.edge.agent.core.channel.adapter.connector.opcua.subscribe;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

/**
 * @author zyq
 * @date 2022/11/15 11:05
 */
public interface ISubscribeCallback {
    /**
     * 订阅消息回调方法
     * @param item
     * @param value
     */
    void callback(UaMonitoredItem item, DataValue value);
}
