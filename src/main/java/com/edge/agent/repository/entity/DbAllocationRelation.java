package com.edge.agent.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zyq
 */
@Data
public class DbAllocationRelation implements Serializable {
    @TableId
    private Long pkAllocationRelation;
    private Long pkAgent;
    private Long pkPlc;
    private Long bufferSize;
}
