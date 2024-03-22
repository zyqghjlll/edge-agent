package com.edge.agent.core.manager;

import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.channel.Channel;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.exception.ConnectorException;

import java.util.List;

/**
 * @author zyq
 */
public class OpcUaChannelManager extends AbstractChannelManager<List<NodeValue>, List<NodeValue>> {

    @Override
    public void channelStatusChanged(Channel<List<NodeValue>, List<NodeValue>> channel) {
        SysLogger.debug("管道状态：" + channel.status() + "管道信息：" + channel);
        switch (channel.status()) {
            case NOT_CONNECTION:
                connect(channel);
                break;
            case CONNECTING:
                // nothing to do
                break;
            case CONNECTION_SUCCEEDED:
                // nothing to do
                break;
            case COMMUNICATION_SUCCEEDED:
                // nothing to do
                break;
            case CONNECTION_FAILED:
            case COMMUNICATION_FAILED:
                disconnect(channel);
                break;
            default:
                // nothing to do
                break;
        }
    }

    private void disconnect(Channel<List<NodeValue>, List<NodeValue>> channel) {
        try {
            channel.reconnect();
        } catch (ConnectorException e) {
            SysLogger.warn(e, "channel reconnect failed. channel:[%s]", channel.toString());
        }
    }

    /**
     * 管道连接
     *
     * @param channel
     */
    private void connect(Channel<List<NodeValue>, List<NodeValue>> channel) {
        try {
            channel.connect();
        } catch (ConnectorException e) {
            SysLogger.error(e, "channel connect error. channel:[%s]", channel.toString());
        }
    }

}
