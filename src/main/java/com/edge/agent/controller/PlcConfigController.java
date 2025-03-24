package com.edge.agent.controller;

import com.edge.agent.common.result.Result;
import com.edge.agent.common.result.ResultResponse;
import com.edge.agent.core.manager.exception.ChannelManagerException;
import com.edge.agent.service.PlcConfigService;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.utils.ThreadPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plcConfig")
public class PlcConfigController {

    @Autowired
    private PlcConfigService plcConfigService;

    @GetMapping(value = "/isAlive")
    public Result<?> isAlive() {
        return ResultResponse.success();
    }

    @PostMapping(value = "/updated")
    public Result<?> updated() {
        ThreadPoolUtil.submitTask(new Runnable() {
            @Override
            public void run() {
                try {
                    plcConfigService.dynamicConfiguration();
                } catch (ChannelManagerException exception) {
                    SysLogger.error(exception, "A error occurred while updating PLC configurations");
                }
            }
        });
        return ResultResponse.success();
    }

    @GetMapping(value = "/updatedGet")
    public Result<?> updatedGet() {
        return updated();
    }

    @PostMapping(value = "/flush")
    public Result<?> flush() {
        try {
            plcConfigService.flush();
        } catch (ChannelManagerException e) {
            return ResultResponse.fail(e.getMessage());
        }
        return ResultResponse.success();
    }
}
