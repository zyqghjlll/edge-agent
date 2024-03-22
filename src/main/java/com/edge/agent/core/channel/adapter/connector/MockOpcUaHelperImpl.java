package com.edge.agent.core.channel.adapter.connector;

import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.channel.exception.ConnectorException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MockOpcUaHelperImpl implements OpcUaHelper {

    @Override
    public void connect(String endpointUrl) throws ConnectorException, ExecutionException, InterruptedException {

    }

    @Override
    public boolean isConnected(String endpointUrl) throws ConnectorException {
        return true;
    }

    @Override
    public void disconnect(String endpointUrl) throws ConnectorException {

    }

    @Override
    public void write(String endpointUrl, NodeId nodeId, Object value) throws OpcUaException {

    }

    @Override
    public List<NodeValue> read(String endpointUrl, List<NodeId> nodeIdList) throws OpcUaException {
        List<NodeValue> result = new ArrayList<>();
        result.add(new NodeValue(null, new NodeId(2, "通道 1.设备 1.标记 1"), StatusCode.GOOD, "true"));
        result.add(new NodeValue(null, new NodeId(2, "通道 1.设备 1.标记 2"), StatusCode.GOOD, "true"));
        result.add(new NodeValue(null, new NodeId(2, "通道 1.设备 1.标记 3"), StatusCode.GOOD, "false"));
        return result;
    }
}
