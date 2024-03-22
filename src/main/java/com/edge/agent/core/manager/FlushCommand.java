package com.edge.agent.core.manager;

import cn.hutool.core.collection.CollUtil;
import com.edge.agent.utils.SysLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyq
 */
public class FlushCommand<U, D> {
    private AbstractChannelManager<U, D> channelManager;

    public FlushCommand(AbstractChannelManager<U, D> channelManager) {
        this.channelManager = channelManager;
    }

    public void flush(List<ChannelConfig<U, D>> targetConfigs) {
        channelManager.removeAll();
        if (CollUtil.isNotEmpty(targetConfigs)) {
            List<ChannelConfig> newlyAdded = new ArrayList<>(targetConfigs);
            channelManager.add(newlyAdded);
        }

        SysLogger.debug("管道管理器动态刷新完成，共计 total:[%s]", channelManager.channelCount());
    }
}
