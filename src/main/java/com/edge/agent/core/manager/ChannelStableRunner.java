package com.edge.agent.core.manager;

import com.edge.agent.common.Runner;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;

/**
 * @author zyq
 */
public class ChannelStableRunner<U, D> extends Runner {

    private AbstractChannelManager<U, D> channelManager;

    public ChannelStableRunner(AbstractChannelManager<U, D> channelManager) {
        super(false, 5000);
        this.channelManager = channelManager;
    }

    @Override
    public void execute() {
        SysLogger.info("MaintainChannel start Time-> " + CommonUtil.getDate() + "-> channel count:" + channelManager.channelCount());
        channelManager.maintainChannel();
    }
}
