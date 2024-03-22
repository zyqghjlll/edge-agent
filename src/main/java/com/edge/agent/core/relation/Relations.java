package com.edge.agent.core.relation;

import lombok.Getter;

/**
 * @author zyq
 */
@Getter
public class Relations {
    private Long pkRelation;
    private Long pkPlc;
    private Long pkAgent;

    public Relations(Long pkRelation, Long pkAgent, Long pkPlc) {
        this.pkRelation = pkRelation;
        this.pkPlc = pkPlc;
        this.pkAgent = pkAgent;
    }
}
