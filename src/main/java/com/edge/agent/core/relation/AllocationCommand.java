package com.edge.agent.core.relation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.relation.allocation.AllocationStrategy;
import com.edge.agent.core.relation.allocation.Participants;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.core.agent.AgentManager;
import com.edge.agent.core.manager.ChannelInstanceRepository;
import com.edge.agent.core.remote.Plc;
import com.edge.agent.core.remote.PlcManager;
import com.edge.agent.core.relation.allocation.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zyq
 */
@Component
public class AllocationCommand {
    @Autowired
    private AgentManager agentManager;
    @Autowired
    private PlcManager plcManager;
    @Autowired
    private ChannelInstanceRepository channelInstanceRepository;
    @Autowired
    private AllocationStrategy<Agent, Plc> allocationStrategy;
    private Long bufferSize = 1024 * 8L;

    public void allocation() {
        Agent agent = agentManager.getCurrent();
        if (ObjectUtil.isEmpty(agent)) {
            SysLogger.debug("未在数据库中配置Agent");
            return;
        }
        List<Agent> agents = agentManager.getByGroupCode(agent.getGroupCode(), agent.getProtocol());
        List<Plc> allPlcList = plcManager.getByGroupCode(agent.getGroupCode(), agent.getProtocol());

        // 删除无用
        List<Long> deletedIds = allPlcList.stream().filter(f -> f.getDeleted() == 1).map(Plc::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deletedIds)) {
            channelInstanceRepository.deleteByPlc(deletedIds);
        }

        // 获取已分配
        List<Plc> allocated = channelInstanceRepository.findByAgents(agents);
        // 差集 待分配
        List<Plc> effectivePlcList = allPlcList.stream().filter(f -> f.getDeleted() == 0).collect(Collectors.toList());
        List<Plc> toBeAllocated = effectivePlcList.stream().filter(f -> !allocated.contains(f)).collect(Collectors.toList());
        // 根据分配策略进行分配
        List<Participants<Agent>> participantsList = new ArrayList<>();
        for (Agent partAgent : agents) {
            List<Plc> byAgent = channelInstanceRepository.findByAgent(partAgent);
            Participants<Agent> participants = new Participants<>(partAgent, partAgent.getMaxConnections(), byAgent.size());
            participantsList.add(participants);
        }
        Resources<Plc> resources = new Resources<>(toBeAllocated);
        HashMap<Agent, Collection<Plc>> allocation = allocationStrategy.allocation(participantsList, resources);
        if (CollUtil.isNotEmpty(allocation)) {
            for (Map.Entry<Agent, Collection<Plc>> item : allocation.entrySet()) {
                channelInstanceRepository.addRelation(item.getKey(), item.getValue(), bufferSize);
            }
        }
    }
}
