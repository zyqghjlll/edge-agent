package com.edge.agent.core.manager;

import com.edge.agent.common.Runner;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.channel.Channel;
import com.edge.agent.core.channel.exception.ConnectorException;

/**
 * @author zyq
 */
public class ChannelReceiveRunner extends Runner {
    private Channel channel;

    public ChannelReceiveRunner(Channel channel,Long msgCycle) {
        super(false, msgCycle);
        this.channel = channel;
    }

    @Override
    public void execute() {
        try {
            if (channel.available()) {
                channel.receive();
            }
        } catch (ConnectorException ex) {
            SysLogger.error(ex, "channel receive data error. channel:[%s]", channel.toString());
        }
    }
}
