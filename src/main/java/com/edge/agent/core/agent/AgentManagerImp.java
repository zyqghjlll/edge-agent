package com.edge.agent.core.agent;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.common.config.AgentConfig;
import com.edge.agent.core.agent.exception.RegisterException;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentManagerImp implements AgentManager {

    @Autowired
    private AgentConfig agentConfig;
    @Autowired
    private AgentRepository agentRepository;
    private Agent current;

    @Override
    public Agent getCurrent() {
        if (ObjectUtil.isEmpty(this.current)) {
            this.current = agentRepository.findByServer(agentConfig.getServerIp(), agentConfig.getServerPort());
            if (ObjectUtil.isEmpty(this.current)) {
                this.current = createAgentByConfig(CommonUtil.getId());
            }
        }

        return this.current;
    }

    public void register() throws RegisterException {
        try {
            Agent byServer = agentRepository.findByServer(agentConfig.getServerIp(), agentConfig.getServerPort());
            if (ObjectUtil.isEmpty(byServer)) {
                this.current = createAgentByConfig(CommonUtil.getId());
                //将agent写入数据库
                agentRepository.addServer(this.current);
            } else {
                this.current = createAgentByConfig(byServer.getId());
                agentRepository.flushServer(this.current);
            }
        } catch (Exception ex) {
            throw new RegisterException(ex);
        }
    }

    private Agent createAgentByConfig(Long pkAgent) {
        Agent agent = new Agent(pkAgent, agentConfig.getServerIp(), agentConfig.getServerPort(), agentConfig.getCode(), agentConfig.getDescription(), agentConfig.getGroupCode(), agentConfig.getProtocol(), agentConfig.getMaxConnections());
        return agent;
    }

    @Override
    public List<Agent> getByGroupCode(String groupCode, String protocol) {
        return agentRepository.findByGroupCode(groupCode, protocol);
    }

    @Override
    public void startMonitoring() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 休眠
                    try {
                        if (ObjectUtil.isNotEmpty(current)) {
                            agentRepository.flushServer(current);
                        } else {
                            register();
                        }
                    } catch (Exception e) {
                        SysLogger.error(e, "Runner运行时异常");
                    } finally {
                        try {
                            Thread.sleep(agentConfig.getUptime());
                        } catch (InterruptedException e) {
                            SysLogger.error(e, "Thread.sleep error.");
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
        thread.setName("agent monitor");
        thread.setDaemon(true);
        thread.start();
    }
}
