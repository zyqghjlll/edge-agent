package com.edge.agent.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.core.agent.AgentManager;
import com.edge.agent.core.channel.ConnectionType;
import com.edge.agent.core.channel.Connector;
import com.edge.agent.core.channel.adapter.connector.MockOpcUaHelperImpl;
import com.edge.agent.core.channel.adapter.connector.OpcUaConnector;
import com.edge.agent.core.channel.adapter.connector.OpcUaHelper;
import com.edge.agent.core.channel.adapter.connector.OpcUaHelperImpl;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.adapter.connector.opcua.OpcUaClientUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.OpcUaClientUtilImpl;
import com.edge.agent.core.channel.adapter.connector.opcua.exception.OpcUaException;
import com.edge.agent.core.consumer.receive.ReceiveDataHandler;
import com.edge.agent.core.consumer.receive.ReceiveDataRepository;
import com.edge.agent.core.consumer.status.StatusHandler;
import com.edge.agent.core.manager.*;
import com.edge.agent.core.manager.buffer.BufferHelper;
import com.edge.agent.core.manager.exception.ChannelManagerException;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.core.remote.Plc;
import com.edge.agent.core.remote.PlcManager;
import com.edge.agent.core.remote.PlcPoint;
import com.edge.agent.core.relation.AllocationCommand;
import com.edge.agent.core.relation.Relations;
import com.edge.agent.utils.SysLogger;
import com.lmax.disruptor.WorkHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zyq
 */
@Component
public class PlcConfigService {
    @Autowired
    private OpcUaChannelManager opcUaChannelManager;
    @Autowired
    private AllocationCommand allocationCommand;
    @Autowired
    private AgentManager agentManager;
    @Autowired
    private PlcManager plcManager;
    @Value("${consumer.db-consumer.consumerCount}")
    private int dbConsumerCount;
    @Value("${consumer.status-consumer.consumerCount}")
    private int statusConsumerCount;
    @Value("${mode.connector-mode}")
    private String connectorMode;
    @Autowired
    private ChannelInstanceRepository channelInstanceRepository;
    @Autowired
    private ReceiveDataRepository receiveDataRepository;
    private OpcUaClientUtil opcUaClientUtil = new OpcUaClientUtilImpl();
    private AtomicLong count = new AtomicLong(0);

    public void dynamicConfiguration() throws ChannelManagerException {
        this.sync();
        this.flush();
    }

    public void sync() {
        plcManager.sync(null);
        plcManager.syncPlcPoints(null);
    }

    public void flush() throws ChannelManagerException {
        allocationCommand.allocation();
        // 根据关系生成配置
        List<ChannelConfig<List<NodeValue>, List<NodeValue>>> targetConfigs = createConfigs();

        FlushCommand<List<NodeValue>, List<NodeValue>> flushCommand2 = new FlushCommand<>(opcUaChannelManager);
        flushCommand2.flush(targetConfigs);
    }

    private List<ChannelConfig<List<NodeValue>, List<NodeValue>>> createConfigs() {
        List<ChannelConfig<List<NodeValue>, List<NodeValue>>> result = new ArrayList<>();
        SysLogger.debug("开始获取根据连接关系刷新配置");

        BufferHelper bufferHelper = getBuffer();
        BufferHelper statusBufferHelper = getStatusBuffer();

        // 查询Mysql，获取当前Agent需要连接PLC列表
        Agent agent = agentManager.getCurrent();
        NetPoint local = null;
        try {
            local = agent.to();
        } catch (NetPointException e) {
            e.printStackTrace();
        }
        List<Relations> relations = channelInstanceRepository.getRelations(agent.getId());
        if (CollUtil.isEmpty(relations)) {
            return result;
        }
        SysLogger.debug("获取连接关系，共计:" + relations.size() + "个");
        for (Relations item : relations) {
            Plc plc = plcManager.getPlc(item.getPkPlc());
            if (ObjectUtil.isEmpty(plc) || ObjectUtil.isEmpty(plc.getIp()) || ObjectUtil.isEmpty(plc.getPort())) {
                continue;
            }

            try {
                List<PlcPoint> plcPointList = plcManager.getPlcPointList(plc);
                OpcUaHelper opcUaHelper;
                if (ObjectUtil.isNotEmpty(connectorMode) && connectorMode.equalsIgnoreCase("prod")) {
                    opcUaHelper = new OpcUaHelperImpl(opcUaClientUtil);
                } else {
                    opcUaHelper = new MockOpcUaHelperImpl();
                }
                Connector connector = new OpcUaConnector(local, local, plc, ConnectionType.LONG_CONNECTION, opcUaHelper, plcPointList);
                ChannelConfig<List<NodeValue>, List<NodeValue>> config
                        = new ChannelConfig<>(item.getPkRelation(), connector)
                        .setReceiveListener(new ReceiveListenerAdapter(bufferHelper, count))
                        .setSendListener(new SendListenerAdapter())
                        .setStatusListener(new StatusListenerAdapter(statusBufferHelper, 1))
                        .setMsgCycle(plc.getMsgCycle());
                result.add(config);
            } catch (OpcUaException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private BufferHelper getStatusBuffer() {
        WorkHandler[] consumers = new WorkHandler[statusConsumerCount];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new StatusHandler(plcManager, channelInstanceRepository);
        }
        return new BufferHelper(1024, consumers);
    }

    private BufferHelper getBuffer() {
        WorkHandler[] dbConsumers = new WorkHandler[dbConsumerCount];
        for (int i = 0; i < dbConsumers.length; i++) {
            dbConsumers[i] = new ReceiveDataHandler(receiveDataRepository);
        }
//        for (int i = 0; i < someConsumers.length; i++) {
//            someConsumers[i] = new SomeEventHandler(agentManager, plcManager, mindSphereManager, upRecordRepository, upErrorDataRepository);
//        }
        return new BufferHelper(1024, dbConsumers);
    }
}
