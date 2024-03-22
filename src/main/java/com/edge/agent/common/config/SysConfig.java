package com.edge.agent.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.manager.OpcUaChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zyq
 */
@Configuration
public class SysConfig {

    @Value("${mode.mindsphere-mode}")
    private String mindsphereMode;
    @Value("${warn-time}")
    private Long warnTime;

    @Bean
    public OpcUaChannelManager opcUaChannelManager() {
        return new OpcUaChannelManager();
    }

}
