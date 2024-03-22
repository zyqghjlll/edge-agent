package com.edge.agent.core.channel.adapter.connector;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.adapter.connector.opcua.OpcUaClientUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.channel.exception.ConnectorException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class OpcUaHelperImpl implements OpcUaHelper {
    private OpcUaClientUtil opcUaClientUtil;
    private Map<String, OpcUaClient> clientMap = new HashMap<>();

    public OpcUaHelperImpl(OpcUaClientUtil opcUaClientUtil) {
        this.opcUaClientUtil = opcUaClientUtil;
    }

    private OpcUaClient createClient(String endpointUrl) throws OpcUaException {
        return this.opcUaClientUtil.createClient(endpointUrl);
    }

    private OpcUaClient getOrCreateClient(String endpointUrl) throws ConnectorException {
        OpcUaClient client = findClient(endpointUrl);
        if (ObjectUtil.isEmpty(client)) {
            try {
                client = this.createClient(endpointUrl);
            } catch (OpcUaException e) {
                throw new ConnectorException(e);
            }
            clientMap.put(endpointUrl, client);
        }
        return client;
    }

    private OpcUaClient findClient(String endpointUrl) {
        OpcUaClient client = null;
        for (Map.Entry<String, OpcUaClient> entry : clientMap.entrySet()) {
            String key = entry.getKey();
            OpcUaClient value = entry.getValue();
            if (key.equals(endpointUrl)) {
                client = value;
                break;
            }
        }
        return client;
    }

    @Override
    public void connect(String endpointUrl) throws ConnectorException, ExecutionException, InterruptedException {
        getOrCreateClient(endpointUrl).connect().get();
    }

    @Override
    public boolean isConnected(String endpointUrl) throws ConnectorException {
        return !getOrCreateClient(endpointUrl).connect().isCancelled();
    }

    @Override
    public void disconnect(String endpointUrl) throws ConnectorException {
        if (!getOrCreateClient(endpointUrl).connect().isCancelled()) {
            getOrCreateClient(endpointUrl).connect().cancel(true);
        }
    }

    @Override
    public void write(String endpointUrl, NodeId nodeId, Object value) throws OpcUaException, ConnectorException {
        this.opcUaClientUtil.write(getOrCreateClient(endpointUrl), nodeId, value);
    }

    @Override
    public List<NodeValue> read(String endpointUrl, List<NodeId> nodeIdList) throws ConnectorException, OpcUaException {
        return this.opcUaClientUtil.read(getOrCreateClient(endpointUrl), nodeIdList);
    }
}
