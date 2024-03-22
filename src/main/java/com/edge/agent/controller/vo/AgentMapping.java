package com.edge.agent.controller.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zyq
 */
@Data
public class AgentMapping implements Serializable {
    private String agentId;
    private String serverIp;
    private String serverPort;
    private List<PlcVo> plcList;
}
