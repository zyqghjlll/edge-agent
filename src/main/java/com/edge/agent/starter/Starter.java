package com.edge.agent.starter;

import com.edge.agent.core.agent.AgentManager;
import com.edge.agent.core.agent.exception.RegisterException;
import com.edge.agent.core.manager.ChannelInstanceRepository;
import com.edge.agent.core.manager.ChannelMonitoringRunner;
import com.edge.agent.core.manager.ChannelStableRunner;
import com.edge.agent.core.manager.OpcUaChannelManager;
import com.edge.agent.core.manager.exception.ChannelManagerException;
import com.edge.agent.service.PlcConfigService;
import com.edge.agent.utils.SysLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zyq
 */
@Component
public class Starter {

    @Autowired
    private AgentManager agentManager;
    @Autowired
    private OpcUaChannelManager channelManager;
    @Autowired
    private ChannelInstanceRepository channelInstanceRepository;
    @Autowired
    private PlcConfigService plcConfigService;


    @PostConstruct
    public void run() {
        try {
            // Agent 注册
            agentManager.register();
            agentManager.startMonitoring();
        } catch (RegisterException e) {
            e.printStackTrace();
            return;
        }

        try {
            //plcConfigService.dynamicConfiguration();
            plcConfigService.flush();
        } catch (ChannelManagerException e) {
            SysLogger.error(e, "dynamicConfiguration失败：ChannelManagerException");
            return;
        } // catch (MindsphereException e) {
//            throw new RuntimeException(e);
//        }

        channelStable();
        channelMonitoring();
    }

    public void channelStable() {
        ChannelStableRunner channelStableRunner = new ChannelStableRunner(channelManager);
        Thread thread = new Thread(channelStableRunner);
        thread.setName("Maintain channel stability");
        thread.setDaemon(true);
        thread.start();
    }

    public void channelMonitoring() {
        ChannelMonitoringRunner channelMonitoringRunner = new ChannelMonitoringRunner(agentManager, channelManager, channelInstanceRepository);
        Thread thread = new Thread(channelMonitoringRunner);
        thread.setName("Channel monitoring");
        thread.setDaemon(true);
        thread.start();
    }
}
