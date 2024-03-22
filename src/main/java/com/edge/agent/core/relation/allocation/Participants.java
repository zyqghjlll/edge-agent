package com.edge.agent.core.relation.allocation;

import lombok.Getter;

/**
 * @author zyq
 */
@Getter
public class Participants<T> {
    private T participantInfo;
    /**
     * 总量
     */
    private int totalQuantity;
    /**
     * 已分配数量
     */
    private int allocatedQuantity;
    /**
     * 可分配数量
     */
    private int distributableQuantity;

    public Participants(T participantInfo, int totalQuantity, int allocatedQuantity) {
        this.participantInfo = participantInfo;
        this.totalQuantity = totalQuantity;
        this.allocatedQuantity = allocatedQuantity;
        this.distributableQuantity = totalQuantity - allocatedQuantity;
    }
}
