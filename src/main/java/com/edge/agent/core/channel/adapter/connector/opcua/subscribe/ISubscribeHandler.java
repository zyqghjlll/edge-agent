package com.edge.agent.core.channel.adapter.connector.opcua.subscribe;

import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 * @author zyq
 * @date 2022/11/15 11:02
 */
public interface ISubscribeHandler {
    boolean subscribe(String endpointUrl, NodeId nodeId, ISubscribeCallback callback) throws OpcUaException;
}
