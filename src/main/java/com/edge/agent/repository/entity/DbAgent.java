package com.edge.agent.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.utils.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zyq
 */
@Data
public class DbAgent implements Serializable {
    @TableId
    private Long pkAgent;
    private String serverIp;
    private int serverPort;
    private String code;
    private String description;
    private String groupCode;
    private String protocol;
    private int maxConnections;
    private Date uptime;
    private int deleted;


    public Agent to() {
        return new Agent(this.pkAgent, this.serverIp, this.serverPort, this.code, this.description, this.groupCode, this.protocol, this.maxConnections);
    }

    public DbAgent from(Agent agent) {
        this.pkAgent = agent.getId();
        this.serverIp = agent.getServerIp();
        this.serverPort = agent.getServerPort();
        this.code = agent.getCode();
        this.description = agent.getDescription();
        this.groupCode = agent.getGroupCode();
        this.protocol = agent.getProtocol();
        this.maxConnections = agent.getMaxConnections();
        this.uptime = CommonUtil.getDate();
        this.deleted = 0;
        return this;
    }
}
