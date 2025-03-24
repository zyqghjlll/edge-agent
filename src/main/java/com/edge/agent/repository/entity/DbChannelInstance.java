package com.edge.agent.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zyq
 */
@Data
public class DbChannelInstance implements Serializable {
    @TableId
    private Long pkChannelInstance;
    private Long pkAllocationRelation;
    private String agentIp;
    private String agentPort;
    private String plcIp;
    private String plcPort;
    private String status;
    private String reason;
    private Date uptime;
    private String syncStatus;
    private Date syncTime;
}
