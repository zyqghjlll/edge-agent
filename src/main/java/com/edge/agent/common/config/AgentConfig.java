package com.edge.agent.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author zyq
 */
@Getter
@Configuration
public class AgentConfig {
    @Value("${server.port}")
    private int serverPort;
    @Value("${agent.code}")
    private String code;
    @Value("${agent.description}")
    private String description;
    @Value("${agent.group-code}")
    private String groupCode;
    @Value("${agent.protocol}")
    private String protocol;
    @Value("${agent.uptime}")
    private Long uptime;
    @Value("${agent.max-connections}")
    private int maxConnections;

    public String getServerIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(addr).getHostAddress();
    }
}
