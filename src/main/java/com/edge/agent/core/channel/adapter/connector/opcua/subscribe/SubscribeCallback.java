package com.edge.agent.core.channel.adapter.connector.opcua.subscribe;

import com.edge.agent.utils.SysLogger;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.stereotype.Component;

/**
 * @author zyq
 * @date 2022/11/11 9:15
 */
@Component
public class SubscribeCallback implements ISubscribeCallback {

    @Override
    public void callback(UaMonitoredItem item, DataValue value) {
        SubscribeEntity subscribeEntity = new SubscribeEntity();
        String endpointUrl = "";
        Integer namespaceIndex = 0;
        String identifier = "";
        String tagValue = "";
        try {
            endpointUrl = item.getClient().getConfig().getEndpoint().getEndpointUrl();
            namespaceIndex = Integer.valueOf(item.getReadValueId().getNodeId().getNamespaceIndex().toString());
            identifier = item.getReadValueId().getNodeId().getIdentifier().toString();
            Variant read = value.getValue();
            tagValue = read.getValue().toString();

            subscribeEntity.setKey(identifier);
            subscribeEntity.setValue(tagValue);
            subscribeEntity.setNameSpaceIndex(namespaceIndex);
            subscribeEntity.setEndpointUrl(endpointUrl);

            SysLogger.debug("OPC订阅回调 解析OPC消息成功，消息数据：[%s]", subscribeEntity.toString());
            processMessages(subscribeEntity);
        } catch (Exception ex) {
            SysLogger.debug("OPC订阅回调 解析OPC消息失败，消息数据-" +
                    "identifier:[%s]，" +
                    "tagValue:[%s]，" +
                    "namespaceIndex:[%s]，" +
                    "endpointUrl:[%s]，" +
                    "异常信息：[%s]", identifier, tagValue, namespaceIndex, endpointUrl, ex.toString());
        }
    }

    private void processMessages(SubscribeEntity subscribeEntity) {
//        final String logHead = "OPC信号处理";
//        OpcSubscribeTag opcSubscribeTag = iOpcSubscribeTag.getSubscribeTags(subscribeEntity.getNameSpaceIndex(), subscribeEntity.getKey());
//        if (ObjectUtil.isEmpty(opcSubscribeTag)) {
//            log.info(String.format("[%s]-未找到数据库配置的标签：[%s]", logHead, subscribeEntity.toString()));
//            return;
//        }
//
//        AbstractSubscribe subscribe = new LineSubscribe(subscribeEntity);
//        subscribe.save();
//
//        List<OpcMementoTag> tags = iOpcMementoTag.getTags(opcSubscribeTag.getPkLine());
//        if (CollUtil.isEmpty(tags)) {
//            log.info("未发现需要取值的标签");
//            return;
//        }
//        List<OpcMementoTagRecord> recordList = new ArrayList<>();
//        Date start = CommonGetter.getDate();
//
//        // 按照endpointUrl分组
//        Map<String, List<OpcMementoTag>> tagMap = tags.stream().collect(Collectors.groupingBy(OpcMementoTag::getEndpointUrl));
//        // map结构转换
//        Map<String, List<NodeId>> nodeIdMap = tagMap.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        mapValue -> {
//                            List<NodeId> result = new ArrayList<>();
//                            mapValue.getValue().forEach(f -> result.add(new NodeId(f.getNameSpaceIndex(), f.getIdentifier())));
//                            return result;
//                        }));
//        // 遍历各个终结点endpointUrl，每个终结点批量获取plc的值。
//        nodeIdMap.entrySet().stream().forEach(item -> {
//            log.info(String.format("开始获取EndpointUrl[%s]的标签采集点取值", item.getKey()));
//            List<NodeValue> nodeValues = clientHandler.readValue(item.getKey(), item.getValue(), 3);
//            if (CollUtil.isEmpty(nodeValues)) {
//                log.error(String.format("EndpointUrl[%s]的标签采集点取值失败", item.getKey()));
//                return;
//            }
//            log.info(String.format("EndpointUrl[%s]的标签采集点取值成功", item.getKey()));
//            nodeValues.forEach(f -> {
//                Optional<OpcMementoTag> anyTag = tags.stream().filter(s -> s.getEndpointUrl().equals(item.getKey()) && new NodeId(s.getNameSpaceIndex(), s.getIdentifier()).equals(f.getNodeId())).findAny();
//                if (anyTag.isPresent()) {
//                    OpcMementoTagRecord tagRecord = new OpcMementoTagRecord(anyTag.get().getPkMementoTag(), subscribe.getEntity().getId(), String.valueOf(f.getValue()));
//                    recordList.add(tagRecord);
//                }
//            });
//        });
//        Date end = CommonGetter.getDate();
//        long ms = DateUtil.betweenMs(start, end);
//        log.info(String.format("[%s]个标签采集点取值消耗时间[%s]毫秒", tags.size(), ms));
//        log.info("插入数据 采集点数据保存");
//        if (CollUtil.isNotEmpty(recordList)) {
//            iOpcMementoTagRecord.saveList(recordList);
//            // 更新订阅信号处理结果
//            subscribe.pendingProcess();
//        }
    }

}
