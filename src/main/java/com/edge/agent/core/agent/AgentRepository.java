package com.edge.agent.core.agent;

import java.util.List;

/**
 * @author zyq
 */
public interface AgentRepository {
    /**
     * 获取Agent信息
     *
     * @param ip
     * @param port
     * @return
     */
    Agent findByServer(String ip, int port);

    /**
     * 根据主题获取Agent列表
     *
     * @param groupCode
     * @param protocol
     * @return
     */
    List<Agent> findByGroupCode(String groupCode, String protocol);

    /**
     * 在Channel实例集合中查找Agent实例
     *
     * @param ip
     * @param port
     * @return
     */
    Agent findInChannel(String ip, int port);

    void addServer(Agent currentAgent);

    void flushServer(Agent currentAgent);
}
