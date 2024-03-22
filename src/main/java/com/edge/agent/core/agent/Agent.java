package com.edge.agent.core.agent;

import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import lombok.Getter;

/**
 * @author zyq
 */
@Getter
public class Agent {
    private Long id;
    private String serverIp;
    private int serverPort;
    private String code;
    private String description;
    private String groupCode;
    private String protocol;
    private int maxConnections;

    public Agent(Long id, String serverIp, int serverPort, String code, String description, String groupCode, String protocol, int maxConnections) {
        this.id = id;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.code = code;
        this.description = description;
        this.groupCode = groupCode;
        this.protocol = protocol;
        this.maxConnections = maxConnections;
    }

    public NetPoint to() throws NetPointException {
        return new NetPoint(this.serverIp, this.serverPort + "", this.code, this.description);
    }
}
