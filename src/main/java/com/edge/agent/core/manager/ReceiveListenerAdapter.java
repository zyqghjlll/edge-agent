package com.edge.agent.core.manager;

import com.edge.agent.core.manager.buffer.BufferHelper;
import com.edge.agent.core.manager.buffer.DataEvent;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.channel.Contact;
import com.edge.agent.core.channel.ReceiveListener;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 管道数据处理
 *
 * @author zyq
 */
public class ReceiveListenerAdapter implements ReceiveListener<List<NodeValue>> {

    private BufferHelper bufferHelper;
    private AtomicLong count;

    public ReceiveListenerAdapter(BufferHelper bufferHelper, AtomicLong count) {
        this.bufferHelper = bufferHelper;
        this.count = count;
    }

    @Override
    public void handle(Contact contact, List<NodeValue> data) {
        if (data.size() > 0) {
            Map<String, List<NodeValue>> dataMap = split(data);
            dataMap.forEach((key, value) -> {
                Long batchNo = CommonUtil.getId();
                SysLogger.debug("batchNo:[%s], 接收数据次数:[%s], 本条长度:[%s], 接收数据时间:[%s]", batchNo, count.getAndIncrement(), data.size(), CommonUtil.getDateString());
                RawInfo<List<NodeValue>> rawInfo = new RawInfo<>(batchNo, contact, value, CommonUtil.getDate());
                push(rawInfo);
                SysLogger.debug("batchNo:[%s], 推入环形数组时间:[%s]", batchNo, CommonUtil.getDateString());
            });
        }
    }

    private Map<String, List<NodeValue>> split(List<NodeValue> rawData) {
        Map<String, List<NodeValue>> result = new HashMap<>();
        if (rawData.isEmpty()) {
            return result;
        }

        String key = null;
        List<NodeValue> subList = null;
        for (int i = 0; i < rawData.size(); i++) {
            NodeValue nodeValue = rawData.get(i);
            if (nodeValue.getNodeId().getIdentifier().equals("source")) {
                key = String.valueOf(rawData.get(i + 2).getValue());
                subList = new ArrayList<>();
                result.put(key, subList);
            }

            if (subList != null) {
                subList.add(nodeValue);
            }
        }

        return result;
    }

    private void push(Object data) {
        DataEvent dataEvent = new DataEvent();
        dataEvent.setId(CommonUtil.getId());
        dataEvent.setData(data);
        dataEvent.setPushTime(CommonUtil.getDate());
        this.bufferHelper.publishEvent(dataEvent);
    }
}
