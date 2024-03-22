package com.edge.agent.repository.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.core.agent.AgentRepository;
import com.edge.agent.repository.mysql.entity.DbAgent;
import com.edge.agent.repository.mysql.mapper.DbAgentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyq
 */
@Repository
public class AgentRepositoryImp implements AgentRepository {

    @Autowired
    private DbAgentMapper agentMapper;

    @Override
    public Agent findByServer(String ip, int port) {
        Agent agent = null;
        DbAgent dbAgent = agentMapper.findByServer(ip, port);
        if (ObjectUtil.isNotEmpty(dbAgent)) {
            agent = dbAgent.to();
        }
        return agent;
    }

    @Override
    public List<Agent> findByGroupCode(String groupCode, String protocol) {
        List<Agent> result = new ArrayList<>();
        List<DbAgent> dbAgents = agentMapper.findByGroupCode(groupCode, protocol);
        if (CollUtil.isEmpty(dbAgents)) {
            return result;
        }
        dbAgents.forEach(dbAgent -> {
            Agent agent = dbAgent.to();
            result.add(agent);
        });
        return result;
    }

    @Override
    public Agent findInChannel(String ip, int port) {
        return null;
    }

    @Override
    public void addServer(Agent currentAgent) {
        DbAgent dbAgent = new DbAgent();
        dbAgent.from(currentAgent);
        agentMapper.insert(dbAgent);
    }

    @Override
    public void flushServer(Agent currentAgent) {
        DbAgent dbAgent = new DbAgent();
        dbAgent.from(currentAgent);
        agentMapper.updateById(dbAgent);
    }
}
