package com.edge.agent.core.channel.adapter.connector.opcua;

import lombok.Getter;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

/**
 * @author zyq
 * @date 2023/1/12 11:17
 */
@Getter
public class NodeValue {
    private OpcUaClient client;
    private NodeId nodeId;
    private StatusCode status;
    private Object value;

    public NodeValue(OpcUaClient client, NodeId nodeId) {
        this.client = client;
        this.nodeId = nodeId;
        this.status = StatusCode.UNCERTAIN;
        this.value = null;
    }

    public NodeValue(OpcUaClient client, NodeId nodeId, StatusCode status, Object value) {
        this(client, nodeId);
        this.status = status;
        this.value = value;
    }

    public void setValue(StatusCode status, Object object) {
        this.status = status;
        this.value = object;
    }

    @Override
    public String toString() {
        return "NodeValue{" +
                "client=" + client +
                ", nodeId=" + nodeId +
                ", status=" + status +
                ", value=" + value +
                '}';
    }
}
