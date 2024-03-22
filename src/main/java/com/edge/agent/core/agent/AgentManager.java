package com.edge.agent.core.agent;

import com.edge.agent.core.agent.exception.RegisterException;

import java.util.List;

public interface AgentManager {

    Agent getCurrent();

    void register() throws RegisterException;

    List<Agent> getByGroupCode(String groupCode, String protocol);

    void startMonitoring();
}
