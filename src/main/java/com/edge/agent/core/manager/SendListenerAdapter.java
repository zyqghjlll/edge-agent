package com.edge.agent.core.manager;

import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.channel.Contact;
import com.edge.agent.core.channel.SendListener;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;

import java.util.List;

/**
 * 管道数据处理
 *
 * @author zyq
 */
public class SendListenerAdapter implements SendListener<List<NodeValue>> {

    @Override
    public void succeed(Contact contact, List<NodeValue> data) {
        SysLogger.debug(String.format("【报文下发】 成功 IP:[%s], port:[%s], send:[%s]", contact.getRemote().getIp(), contact.getRemote().getPort(), data));
    }

    @Override
    public void failed(Contact contact, List<NodeValue> data, Exception exception) {
        SysLogger.debug(String.format("【报文下发】 失败 IP:[%s], port:[%s], send:[%s], 失败原因: [%s]", contact.getRemote().getIp(), contact.getRemote().getPort(), data, exception.getMessage()));
        SysLogger.error(exception, String.format("【报文下发】 失败 IP:[%s], port:[%s], send:[%s]", contact.getRemote().getIp(), contact.getRemote().getPort(), data));
    }
}
