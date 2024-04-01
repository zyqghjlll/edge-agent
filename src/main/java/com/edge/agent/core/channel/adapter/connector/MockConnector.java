package com.edge.agent.core.channel.adapter.connector;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.channel.exception.ConnectorException;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.core.channel.ConnectionType;
import com.edge.agent.core.channel.Connector;
import com.edge.agent.core.channel.Contact;
import com.edge.agent.core.remote.PlcPoint;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zyq
 */
public class MockConnector extends Connector<List<NodeValue>, List<NodeValue>> {
    private NetPoint server;
    private NetPoint local;
    private NetPoint remote;
    private Socket socket;
    private ConnectionType connectionType;

    public MockConnector(NetPoint server, NetPoint local, NetPoint remote, ConnectionType connectionType, List<PlcPoint> plcPointList) {
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
                // 获取Socket的输入流和输出流
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                // 关闭输入流
                inputStream.close();
                // 关闭输出流
                outputStream.close();

                this.socket.close();
            }
        } catch (IOException ex) {
            throw new ConnectorException(ex.getMessage());
        }
    }

    @Override
    protected void send(List<NodeValue> sendData) throws ConnectorException {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NodeValue> receive() throws ConnectorException {
        List<NodeValue> result = new ArrayList<>();
        try {
            result.add(0, new NodeValue(null, new NodeId(2, "source"), StatusCode.GOOD, ""));
            result.add(1, new NodeValue(null, new NodeId(2, "target"), StatusCode.GOOD, ""));
            result.add(2, new NodeValue(null, new NodeId(2, "type"), StatusCode.GOOD, "OPC"));
            result.add(3, new NodeValue(null, new NodeId(2, "time"), StatusCode.GOOD, timeStampFormat(CommonUtil.getDate())));
            result.add(4, new NodeValue(null, new NodeId(2, "space"), StatusCode.GOOD, ""));
            result.add(4, new NodeValue(null, new NodeId(2, ""), StatusCode.GOOD, "true"));
            result.add(4, new NodeValue(null, new NodeId(2, ""), StatusCode.GOOD, "true"));
            result.add(4, new NodeValue(null, new NodeId(2, ""), StatusCode.GOOD, "false"));
        } catch (Exception ex) {
            throw new ConnectorException(ex.getMessage());
        }
        return result;
    }

    private String timeStampFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String result = sdf.format(date);
        return result;
    }
}
