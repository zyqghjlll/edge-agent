package com.edge.agent.core.channel;

import com.edge.agent.core.channel.exception.ConnectorException;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import lombok.Getter;

/**
 * @author zyq
 */
@Getter
public abstract class Connector<U, D> {
    private final NetPoint server;
    /**
     * 本地服务器
     */
    private final NetPoint local;
    /**
     * 远程服务器
     */
    private final NetPoint remote;
    /**
     * 连接类型
     */
    private final ConnectionType connectionType;

    protected Connector(NetPoint server, NetPoint local, NetPoint remote, ConnectionType connectionType) {
        this.server = server;
        this.local = local;
        this.remote = remote;
        this.connectionType = connectionType;
    }

    /**
     * 获取连接触点
     *
     * @return
     * @throws NetPointException
     */
    public abstract Contact contact() throws NetPointException, ConnectorException;

    /**
     * 连接
     *
     * @throws ConnectorException
     * @description 创建连接
     */
    protected abstract void connect() throws ConnectorException;

    /**
     * 是否连接
     *
     * @return
     */
    protected abstract boolean isConnected() throws ConnectorException;

    /**
     * 断开连接
     *
     * @throws ConnectorException
     */
    protected abstract void disconnect() throws ConnectorException;

    /**
     * 发送数据
     *
     * @param sendData
     * @throws ConnectorException
     */
    protected abstract void send(D sendData) throws ConnectorException;

    /**
     * 接收数据
     *
     * @return
     * @throws ConnectorException
     */
    protected abstract U receive() throws ConnectorException;
}
