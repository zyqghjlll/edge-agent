package com.edge.agent.core.manager;

import cn.hutool.core.collection.CollUtil;
import com.edge.agent.common.Runner;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.agent.AgentManager;

import java.util.List;

/**
 * @author zyq
 */
public class ChannelMonitoringRunner extends Runner {

    private AgentManager agentManager;
    private AbstractChannelManager channelManager;
    private ChannelInstanceRepository channelInstanceRepository;

    public ChannelMonitoringRunner(AgentManager agentManager, AbstractChannelManager channelManager, ChannelInstanceRepository channelInstanceRepository) {
        super(false, 10 * 1000L);
        this.agentManager = agentManager;
        this.channelManager = channelManager;
        this.channelInstanceRepository = channelInstanceRepository;
    }

    @Override
    public void execute() {
        SysLogger.debug("Channel monitoring start Time-> " + CommonUtil.getDate() + "-> channel count:" + channelManager.channelCount());
        List<Monitoring> monitoring = channelManager.monitoring();
        if (CollUtil.isNotEmpty(monitoring)) {
            channelInstanceRepository.uptime(agentManager.getCurrent(), monitoring);
        }
    }
}
