package com.edge.agent.core.channel.adapter.connector;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.adapter.connector.opcua.OpcUaClientUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.channel.exception.ConnectorException;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.core.channel.ConnectionType;
import com.edge.agent.core.channel.Connector;
import com.edge.agent.core.channel.Contact;
import com.edge.agent.core.plc.PlcPoint;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author zyq
 */
public class MockOpcUaConnector extends Connector<List<NodeValue>, List<NodeValue>> {
    private final NetPoint server;
    private final NetPoint local;
    private final NetPoint remote;
    private final ConnectionType connectionType;
    private Map<String, OpcUaClient> clientMap = new HashMap<>();
    private OpcUaClientUtil opcUaClientUtil;
    private Map<String, Map<String, List<PlcPoint>>> groupMap;
    private List<PlcPoint> plcPointList;

    public MockOpcUaConnector(NetPoint server, NetPoint local, NetPoint remote, ConnectionType connectionType, OpcUaClientUtil opcUaClientUtil, List<PlcPoint> plcPointList) throws OpcUaException {
        super(server, local, remote, connectionType);
        this.server = server;
        this.local = local;
        this.remote = remote;
        this.connectionType = connectionType;
        this.opcUaClientUtil = opcUaClientUtil;
        this.plcPointList = plcPointList;
        this.groupMap = grouping(plcPointList);
    }

    private OpcUaClient getOrCreateClient(String endpointUrl) throws ConnectorException {
        OpcUaClient client = getClient(endpointUrl);
        if (ObjectUtil.isEmpty(client)) {
            try {
                client = this.opcUaClientUtil.createClient(endpointUrl);
            } catch (OpcUaException e) {
                throw new ConnectorException(e);
            }
            clientMap.put(endpointUrl, client);
        }
        return client;
    }

    private OpcUaClient getClient(String endpointUrl) {
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

    private List<String> getEndpointUrl() {
        List<String> result = new ArrayList<>();
        if (CollUtil.isEmpty(this.plcPointList)) {
            return result;
        }
        Map<String, List<PlcPoint>> map = this.plcPointList.stream().collect(Collectors.groupingBy(PlcPoint::getEndpointUrl));
        return new ArrayList<>(map.keySet());
    }

    private List<String> getEndpointUrl(int namespaceIndex, String identifier) {
        List<String> result = new ArrayList<>();
        if (CollUtil.isEmpty(this.plcPointList)) {
            return result;
        }
        Map<String, List<PlcPoint>> map = this.plcPointList.stream()
                .filter(f -> Integer.parseInt(f.getNamespaceIndex()) == namespaceIndex && f.getIdentifier().equals(identifier))
                .collect(Collectors.groupingBy(PlcPoint::getEndpointUrl));
        return new ArrayList<>(map.keySet());
    }

    @Override
    public Contact contact() throws NetPointException, ConnectorException {
        NetPoint localContact = local;
        NetPoint remoteContact = remote;
        if (isConnected()) {
            localContact = new NetPoint(this.local.getIp(), String.valueOf(this.local.getPort()));
            remoteContact = new NetPoint(this.remote.getIp(), String.valueOf(this.remote.getPort()));
        }
        return new Contact(localContact, remoteContact);
    }

    @Override
    public void connect() throws ConnectorException {
        try {
            List<String> endpointUrlList = getEndpointUrl();
            if (CollUtil.isEmpty(endpointUrlList)) {
                throw new ConnectorException("没有要连接的终结点");
            }

            for (String url : endpointUrlList) {
                getOrCreateClient(url).connect().get();
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    protected boolean isConnected() throws ConnectorException {
        List<String> endpointUrlList = getEndpointUrl();
        if (CollUtil.isEmpty(endpointUrlList)) {
            return true;
        }

        boolean result = true;
        for (String url : endpointUrlList) {
            if (!getOrCreateClient(url).connect().isCancelled()) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public void disconnect() throws ConnectorException {
        try {
            List<String> endpointUrlList = getEndpointUrl();
            if (CollUtil.isEmpty(endpointUrlList)) {
                return;
            }
            for (String url : endpointUrlList) {
                if (!getOrCreateClient(url).connect().isCancelled()) {
                    getOrCreateClient(url).connect().cancel(true);
                }
            }

        } catch (Exception ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    protected void send(List<NodeValue> sendData) throws ConnectorException {
        try {
            if (ObjectUtil.isEmpty(sendData)) {
                return;
            }

            for (NodeValue nodeValue : sendData) {
                List<String> endpointUrlList = this.getEndpointUrl(Integer.parseInt(nodeValue.getNodeId().getNamespaceIndex().toString()), nodeValue.getNodeId().getIdentifier().toString());
                for (String url : endpointUrlList) {
                    this.opcUaClientUtil.write(getOrCreateClient(url), nodeValue.getNodeId(), nodeValue.getValue());
                }
            }
        } catch (Exception ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    public List<NodeValue> receive() throws ConnectorException {
        List<NodeValue> result = new ArrayList<>();
        try {
            if (CollUtil.isEmpty(groupMap)) {
                return new ArrayList<>();
            }
            for (Map.Entry<String, Map<String, List<PlcPoint>>> pointTypeGroup : groupMap.entrySet()) {
                List<NodeValue> result1 = createResult(pointTypeGroup.getKey());
                for (Map.Entry<String, List<PlcPoint>> clientGroup : pointTypeGroup.getValue().entrySet()) {
                    List<NodeId> nodeIdList = clientGroup.getValue()
                            .stream()
                            .map(m -> new NodeId(Integer.parseInt(m.getNamespaceIndex()), m.getIdentifier()))
                            .collect(Collectors.toList());
                    List<NodeValue> collect = this.opcUaClientUtil.read(getOrCreateClient(clientGroup.getKey()), nodeIdList);
                    updateResult(result1, collect);
                }

                result.addAll(result1);
            }
        } catch (Exception ex) {
            throw new ConnectorException(ex.getMessage());
        }
        return result;
    }

    private Map<String, Map<String, List<PlcPoint>>> grouping(List<PlcPoint> plcPointList) {
        Map<String, Map<String, List<PlcPoint>>> result = new HashMap<>();
        Map<String, List<PlcPoint>> map = plcPointList.stream().collect(Collectors.groupingBy(PlcPoint::getPointType));
        for (Map.Entry<String, List<PlcPoint>> entry : map.entrySet()) {
            String key = entry.getKey();
            Map<String, List<PlcPoint>> pointMap = entry.getValue().stream().collect(Collectors.groupingBy(PlcPoint::getEndpointUrl));
            result.put(key, pointMap);
        }

        return result;
    }

    private List<NodeValue> createResult(String type) {
        List<NodeValue> result = new ArrayList<>();
        result.add(0, new NodeValue(null, new NodeId(2, "source"), StatusCode.GOOD, ""));
        result.add(1, new NodeValue(null, new NodeId(2, "target"), StatusCode.GOOD, ""));
        result.add(2, new NodeValue(null, new NodeId(2, "type"), StatusCode.GOOD, type));
        result.add(3, new NodeValue(null, new NodeId(2, "time"), StatusCode.GOOD, timeStampFormat(CommonUtil.getDate())));
        result.add(4, new NodeValue(null, new NodeId(2, "space"), StatusCode.GOOD, ""));

        List<PlcPoint> collect = plcPointList.stream().filter(f -> f.getPointType().equals(type)).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return result;
        }


        int maxIndex = collect.stream().max(Comparator.comparing(PlcPoint::getSort)).get().getSort();
        for (int i = 0; i <= maxIndex; i++) {
            int finalI = i;
            Optional<PlcPoint> any = plcPointList.stream().filter(f -> finalI == f.getSort()).findAny();
            if (any.isPresent()) {
                result.add(new NodeValue(null, new NodeId(Integer.parseInt(any.get().getNamespaceIndex()), any.get().getIdentifier()), StatusCode.GOOD, ""));
            } else {
                result.add(new NodeValue(null, new NodeId(Integer.parseInt(any.get().getNamespaceIndex()), "unknown"), StatusCode.GOOD, ""));
            }
        }
        return result;
    }

    private void updateResult(List<NodeValue> target, List<NodeValue> addition) {
        for (NodeValue nodeValue : target) {
            Optional<NodeValue> first = addition.stream().filter(f -> sameNode(f, nodeValue)).findFirst();
            first.ifPresent(value -> nodeValue.setValue(value.getStatus(), value.getValue()));
        }
    }

    private boolean sameNode(NodeValue n1, NodeValue n2) {
        if (n1.getNodeId().getNamespaceIndex().equals(n2.getNodeId().getNamespaceIndex()) && n1.getNodeId().getIdentifier().equals(n2.getNodeId().getIdentifier())) {
            return true;
        }
        return false;
    }

    private String timeStampFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String result = sdf.format(date);
        return result;
    }

}
