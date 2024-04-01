package com.edge.agent.core.manager;

import com.edge.agent.core.relation.Relations;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.core.remote.Plc;

import java.util.Collection;
import java.util.List;

/**
 * @author zyq
 */
public interface ChannelInstanceRepository {

    List<Plc> findByAgents(List<Agent> agents);

    void addRelation(Agent agent, Collection<Plc> plcs, Long bufferSize);

    List<Plc> findByPlc(Agent agent, Collection<String> assetIds);

    List<Plc> findByAgent(Agent agent);

    List<Relations> getRelations(Long pkAgent);

    /**
     * 更新管道活跃时间
     *
     * @param monitoring
     */
    void uptime(Agent agent, List<Monitoring> monitoring);

    void deleteByPlc(List<Long> deletedIds);

    void deleteByRelationIds(List<Long> relationIds);

    void syncInfo(Plc plc, String message);
}
