package com.edge.agent.core.manager;

import com.edge.agent.core.netpoint.NetPoint;
import lombok.Data;

import java.util.Date;

/**
 * @author zyq
 */
@Data
public class Monitoring {
    private Long pkRelation;
    private NetPoint server;
    private NetPoint local;
    private NetPoint remote;
    private String status;
    private String reason;
    private Date uptime;
}
