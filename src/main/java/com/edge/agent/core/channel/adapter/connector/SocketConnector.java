package com.edge.agent.core.channel.adapter.connector;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.channel.exception.ConnectorException;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.core.channel.ConnectionType;
import com.edge.agent.core.channel.Connector;
import com.edge.agent.core.channel.Contact;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author zyq
 */
public class SocketConnector extends Connector<byte[], byte[]> {
    private NetPoint server;
    private NetPoint local;
    private NetPoint remote;
    private Socket socket;
    private ConnectionType connectionType;

    public SocketConnector(NetPoint server, NetPoint local, NetPoint remote, ConnectionType connectionType) {
        super(server, local, remote, connectionType);
        this.server = server;
        this.local = local;
        this.remote = remote;
        this.connectionType = connectionType;
    }


    @Override
    public Contact contact() throws NetPointException {
        NetPoint localContact = local;
        NetPoint remoteContact = remote;
        if (isConnected()) {
            localContact = new NetPoint(this.local.getIp(), this.socket.getLocalPort() + "");
            remoteContact = new NetPoint(this.remote.getIp(), this.remote.getPort() + "");
        }
        return new Contact(localContact, remoteContact);
    }

    @Override
    public void connect() throws ConnectorException {
        try {
            this.socket = new Socket(InetAddress.getByName(remote.getIp()), remote.getPort());
        } catch (IOException ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    protected boolean isConnected() {
        boolean result = false;
        if (ObjectUtil.isNotEmpty(this.socket)) {
            if (this.socket.isConnected()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void disconnect() throws ConnectorException {
        try {
            if (ObjectUtil.isEmpty(this.socket)) {
                return;
            }
            if (!this.socket.isClosed()) {
                this.socket.getOutputStream().close();
                this.socket.getInputStream().close();
                this.socket.close();
            }
        } catch (IOException ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    public void send(byte[] sendData) throws ConnectorException {
        try {
            if (ObjectUtil.isEmpty(sendData)) {
                return;
            }
            // 使用输出流发送消息
            OutputStream outputStream = socket.getOutputStream();
            // 发送消息
            outputStream.write(sendData);
        } catch (IOException ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    public byte[] receive() throws ConnectorException {
        byte[] bytes = new byte[0];
        try {
            // 得到客户端发送的流对象
            InputStream inputStream = socket.getInputStream();
            if (inputStream.available() > 0) {
                // 获取客户端发送的信息
                bytes = new byte[inputStream.available()];
                inputStream.read(bytes, 0, bytes.length);
            }
        } catch (IOException ex) {
            throw new ConnectorException(ex.getMessage());
        }
        return bytes;
    }
}
