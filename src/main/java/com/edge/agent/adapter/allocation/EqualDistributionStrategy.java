package com.edge.agent.adapter.allocation;


import cn.hutool.core.collection.CollUtil;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.core.remote.Plc;
import com.edge.agent.core.relation.allocation.AllocationStrategy;
import com.edge.agent.core.relation.allocation.Participants;
import com.edge.agent.core.relation.allocation.Resources;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 平均分配策略
 *
 * @author zyq
 */
@Component
public class EqualDistributionStrategy implements AllocationStrategy<Agent, Plc> {

    @Override
    public HashMap<Agent, Collection<Plc>> allocation(List<Participants<Agent>> participants, Resources<Plc> resource) {
        HashMap<Agent, Collection<Plc>> result = new HashMap<>();
        if (CollUtil.isNotEmpty(participants) && CollUtil.isNotEmpty(resource.getDistributableResources())) {
            for (int i = 0; i < participants.size(); i++) {
                int distributableQuantity = participants.get(i).getDistributableQuantity();
                if (distributableQuantity > 0) {
                    List<Plc> extract = resource.extract(distributableQuantity);
                    result.put(participants.get(i).getParticipantInfo(), extract);
                }
            }
        }
        return result;
    }
}
