package com.edge.agent.core.channel.adapter.connector.opcua;

import cn.hutool.core.collection.CollUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.channel.adapter.connector.opcua.subscribe.ISubscribeCallback;
import com.google.common.collect.ImmutableList;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.springframework.util.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author zyq
 */
public class OpcUaClientUtilImpl implements OpcUaClientUtil {

    /**
     * 创建OPC UA客户端
     *
     * @param ip
     * @param port
     * @param suffix
     * @return
     * @throws Exception
     */
    @Override
    public OpcUaClient createClient(String ip, String port, String suffix) throws OpcUaException {
        String endpointUrl = "opc.tcp://" + ip + ":" + port + suffix;
        return createClient(endpointUrl);
    }

    @Override
    public OpcUaClient createClient(String endpointUrl) throws OpcUaException {
        OpcUaClient opcUaClient = null;
        try {
            Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
            Files.createDirectories(securityTempDir);
            if (!Files.exists(securityTempDir)) {
                throw new OpcUaException("unable to create security dir: " + securityTempDir);
            }

            // 搜索OPC节点
            List<EndpointDescription> endpoints = null;
            try {
                endpoints = DiscoveryClient.getEndpoints(endpointUrl).get();
            } catch (RuntimeException e) {
                // try the explicit discovery endpoint as well
                String discoveryUrl = endpointUrl;

                if (!discoveryUrl.endsWith("/")) {
                    discoveryUrl += "/";
                }
                discoveryUrl += "discovery";

                endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
            }

            EndpointDescription endpoint = endpoints.stream()
                    .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                    .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

            OpcUaClientConfig config = OpcUaClientConfig.builder()
                    .setApplicationName(LocalizedText.english("opcUaClient"))
                    .setApplicationUri(endpointUrl)
                    // 安全策略等配置
                    .setEndpoint(endpoint)
                    // 证书，用户名认证
                    // .setCertificate(loader.getClientCertificate()).setKeyPair(loader.getClientKeyPair())
                    // .setIdentityProvider(new UsernameProvider("zyq", "123456"))
                    // 匿名验证
                    .setIdentityProvider(new AnonymousProvider())
                    .setRequestTimeout(Unsigned.uint(10000)).build();
            opcUaClient = OpcUaClient.create(config);
            opcUaClient.connect().get();
            Thread.sleep(2000); // 线程休眠一下再返回对象，给创建过程一个时间。
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
        return opcUaClient;
    }

    /**
     * 遍历树形节点
     *
     * @param client OPC UA客户端
     * @param nodeId 节点
     * @throws OpcUaException
     */
    @Override
    public List<NodeId> list(OpcUaClient client, NodeId nodeId) throws OpcUaException {
        List<NodeId> result = new ArrayList<>();
        try {
            recursion(client, nodeId, result);
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
        return result;
    }

    @Override
    public List<NodeId> find(OpcUaClient client, NodeId nodeId, int namespaceIndex, String idLike) throws
            OpcUaException {
        List<NodeId> result = new ArrayList<>();
        try {
            recursion(client, nodeId, namespaceIndex, idLike, result);
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
        return result;
    }

    private void recursion(OpcUaClient client, NodeId nodeId, List<NodeId> nodeIdList) throws OpcUaException {
        try {
            List<? extends UaNode> nodes;
            if (nodeId == null) {
                nodes = client.getAddressSpace().browseNodes(Identifiers.ObjectsFolder);
            } else {
                nodes = client.getAddressSpace().browseNodes(nodeId);
            }
            if (CollUtil.isEmpty(nodes)) {
                return;
            }
            List<NodeId> nodeIds = nodes.stream()
                    // 排除系统行性节点，这些系统性节点名称一般都是以"_"开头
                    .filter(f -> !Objects.requireNonNull(f.getBrowseName().getName()).contains("_"))
                    .map(UaNode::getNodeId)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(nodeIds)) {
                nodeIdList.addAll(nodeIds);
                for (NodeId ni : nodeIds) {
                    recursion(client, ni, nodeIdList);
                }
            }
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
    }

    private void recursion(OpcUaClient client, NodeId nodeId, int namespaceIndex, String
            idLike, List<NodeId> nodeIdList) throws OpcUaException {
        try {
            List<? extends UaNode> nodes;
            if (nodeId == null) {
                nodes = client.getAddressSpace().browseNodes(Identifiers.ObjectsFolder);
            } else {
                nodes = client.getAddressSpace().browseNodes(nodeId);
            }
            if (CollUtil.isEmpty(nodes)) {
                return;
            }
            List<NodeId> nodeIds = nodes.stream()
                    // 排除系统行性节点，这些系统性节点名称一般都是以"_"开头
                    .filter(f -> !Objects.requireNonNull(f.getBrowseName().getName()).contains("_"))
                    .map(UaNode::getNodeId)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(nodeIds)) {
                nodeIdList.addAll(nodeIds.stream().filter(f -> f.getNamespaceIndex().equals(namespaceIndex) && f.getIdentifier().toString().contains(idLike)).collect(Collectors.toList()));
                for (NodeId ni : nodeIds) {
                    recursion(client, ni, nodeIdList);
                }
            }
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
    }

    @Override
    public boolean isExist(OpcUaClient client, NodeId nodeId) throws OpcUaException {
        List<NodeId> list = list(client, nodeId);
        Optional<NodeId> any = list.stream().filter(f -> f.equals(nodeId)).findAny();
        return any.isPresent();
    }

    /**
     * 读取指定节点的值的重载方法
     *
     * @param client
     * @param nodeId
     * @throws Exception
     */
    @Override
    public NodeValue read(OpcUaClient client, NodeId nodeId) throws OpcUaException {
        NodeValue result = new NodeValue(client, nodeId);
        try {
            //读取节点数据
            DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();
            result.setValue(value.getStatusCode(), value.getValue().getValue());
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
        return result;
    }

    /**
     * 批量获取值
     *
     * @param client
     * @param nodeIds
     * @return
     */
    public List<NodeValue> read(OpcUaClient client, List<NodeId> nodeIds) throws OpcUaException {
        List<NodeValue> result = new ArrayList<>();
        try {
            // dataValues按顺序定义的值，每个值里面需要重新判断类型
            List<DataValue> dataValues = client.readValues(0.0, TimestampsToReturn.Both, nodeIds).get();

            for (int i = 0; i < nodeIds.size(); i++) {
                DataValue dataValue = dataValues.get(i);
                NodeValue nodeValue = new NodeValue(client, nodeIds.get(i));
                nodeValue.setValue(dataValue.getStatusCode(), dataValue.getValue().getValue());
                result.add(nodeValue);
            }
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
        return result;
    }

    /**
     * 写入节点数据
     *
     * @param client
     * @param nodeId
     * @param value
     * @throws Exception
     */
    @Override
    public void write(OpcUaClient client, NodeId nodeId, Object value) throws OpcUaException {
        try {
            // 创建数据对象,此处的数据对象一定要定义类型，不然会出现类型错误，导致无法写入
            DataValue newValue = new DataValue(new Variant(value), null, null);
            //写入节点数据
            StatusCode statusCode = client.writeValue(nodeId, newValue).join();
            if (statusCode.isBad()) {
                throw new OpcUaException(statusCode.toString());
            }
        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
    }

    /**
     * 订阅单个节点的重载方法
     *
     * @param client
     * @param nodeId
     * @param resubscribeWhenBreak
     * @throws Exception
     */
    @Override
    public void subscribe(OpcUaClient client, NodeId nodeId, ISubscribeCallback callback,
                          boolean resubscribeWhenBreak) throws OpcUaException {
        try {
            // 查询订阅对象，没有则创建
            UaSubscription subscription;
            ImmutableList<UaSubscription> subscriptionList = client.getSubscriptionManager().getSubscriptions();
            if (CollectionUtils.isEmpty(subscriptionList)) {
                // 创建发布间隔1000ms的订阅对象
                subscription = client.getSubscriptionManager().createSubscription(1000.0).get();
            } else {
                subscription = subscriptionList.get(0);
            }

            // 监控项请求列表
            List<MonitoredItemCreateRequest> requests = new ArrayList<>();

            MonitoringParameters parameters = new MonitoringParameters(subscription.nextClientHandle(), 1000.0, null, Unsigned.uint(10), true);
            // 创建订阅的变量， 创建监控项请求
            // 节点
            ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);

            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
            requests.add(request);

            UaSubscription.ItemCreationCallback onItemCreated = getSubCallback((item, value) -> {
                try {
                    callback.callback(item, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 创建监控项，并且注册变量值改变时候的回调函数
            subscription.createMonitoredItems(TimestampsToReturn.Both, requests, onItemCreated).get();
            if (resubscribeWhenBreak) {
                //添加订阅监听器，用于处理断线重连后的订阅问题
                client.getSubscriptionManager().addSubscriptionListener(new CustomSubscriptionListener(client, nodeId, callback, resubscribeWhenBreak));
            }

        } catch (Exception ex) {
            throw new OpcUaException(ex);
        }
    }

    /**
     * 批量订阅
     *
     * @param client
     * @throws Exception
     */
    @Override
    public void subscribe(OpcUaClient client, List<NodeId> nodeIdList, ISubscribeCallback callback,
                          boolean resubscribeWhenBreak) throws OpcUaException {
        for (NodeId nodeId : nodeIdList) {
            subscribe(client, nodeId, callback, resubscribeWhenBreak);
        }
    }

    private UaSubscription.ItemCreationCallback getSubCallback(UaMonitoredItem.ValueConsumer valueConsumer) {
        UaSubscription.ItemCreationCallback onItemCreated = (item, id) -> item.setValueConsumer(valueConsumer);
        return onItemCreated;
    }

    /**
     * 自定义订阅监听
     */
    private class CustomSubscriptionListener implements UaSubscriptionManager.SubscriptionListener {
        private final OpcUaClient client;
        private final NodeId nodeId;
        private final ISubscribeCallback callback;
        private final boolean resubscribeWhenBreak;

        CustomSubscriptionListener(OpcUaClient client, NodeId nodeId, ISubscribeCallback callback, boolean resubscribeWhenBreak) {
            this.client = client;
            this.nodeId = nodeId;
            this.callback = callback;
            this.resubscribeWhenBreak = resubscribeWhenBreak;
        }

        public void onKeepAlive(UaSubscription subscription, DateTime publishTime) {
            System.out.println("onKeepAlive");
        }

        public void onStatusChanged(UaSubscription subscription, StatusCode status) {
            System.out.println("onStatusChanged");
        }

        public void onPublishFailure(UaException exception) {
            System.out.println("onPublishFailure");
        }

        public void onNotificationDataLost(UaSubscription subscription) {
            System.out.println("onNotificationDataLost");
        }

        /**
         * 重连时，尝试恢复之前的订阅失败时，会调用此方法
         *
         * @param uaSubscription 订阅
         * @param statusCode     状态
         */
        public void onSubscriptionTransferFailed(UaSubscription uaSubscription, StatusCode statusCode) {
            System.out.println("恢复订阅失败 需要重新订阅");
            //在回调方法中重新订阅
            try {
                subscribe(client, nodeId, callback, resubscribeWhenBreak);
            } catch (OpcUaException e) {
                System.out.println("重新订阅失败");
                throw new RuntimeException(e);
            }
        }
    }
}
