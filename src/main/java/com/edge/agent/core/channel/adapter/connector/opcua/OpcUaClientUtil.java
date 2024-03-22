package com.edge.agent.core.channel.adapter.connector.opcua;

import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.channel.adapter.connector.opcua.subscribe.ISubscribeCallback;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.List;

public interface OpcUaClientUtil {
    OpcUaClient createClient(String ip, String port, String suffix) throws OpcUaException;

    OpcUaClient createClient(String endpointUrl) throws OpcUaException;

    List<NodeId> list(OpcUaClient client, NodeId nodeId) throws OpcUaException;

    List<NodeId> find(OpcUaClient client, NodeId nodeId, int namespaceIndex, String idLike) throws OpcUaException;

    boolean isExist(OpcUaClient client, NodeId nodeId) throws OpcUaException;

    NodeValue read(OpcUaClient client, NodeId nodeId) throws OpcUaException;

    List<NodeValue> read(OpcUaClient client, List<NodeId> nodeIds) throws OpcUaException;

    void write(OpcUaClient client, NodeId nodeId, Object value) throws OpcUaException;

    void subscribe(OpcUaClient client, NodeId nodeId, ISubscribeCallback callback, boolean resubscribeWhenBreak) throws OpcUaException;

    void subscribe(OpcUaClient client, List<NodeId> nodeIdList, ISubscribeCallback callback, boolean resubscribeWhenBreak) throws OpcUaException;
}
