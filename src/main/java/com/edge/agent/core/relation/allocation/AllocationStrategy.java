package com.edge.agent.core.relation.allocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 分配策略
 *
 * @author zyq
 */
public interface AllocationStrategy<P, R> {
    /**
     * 执行分配
     *
     * @param participantsList 参与者
     * @param resource     资源
     * @return
     */
    HashMap<P, Collection<R>> allocation(List<Participants<P>> participantsList, Resources<R> resource);
}
