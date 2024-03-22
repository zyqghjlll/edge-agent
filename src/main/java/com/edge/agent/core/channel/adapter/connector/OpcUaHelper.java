package com.edge.agent.core.channel.adapter.connector;

import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.channel.exception.ConnectorException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OpcUaHelper {

    void connect(String endpointUrl) throws ConnectorException, ExecutionException, InterruptedException;

    boolean isConnected(String endpointUrl) throws ConnectorException;

    void disconnect(String endpointUrl) throws ConnectorException;

    void write(String endpointUrl, NodeId nodeId, Object value) throws OpcUaException, ConnectorException;

    List<NodeValue> read(String endpointUrl, List<NodeId> nodeIdList) throws OpcUaException, ConnectorException;
}
