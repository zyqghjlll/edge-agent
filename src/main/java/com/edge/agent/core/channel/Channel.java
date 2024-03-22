package com.edge.agent.core.channel;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.channel.exception.ConnectorException;
import com.edge.agent.core.channel.exception.FeedbackException;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.utils.SysLogger;
import lombok.Getter;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 通信管道
 *
 * @author zyq
 */
@Getter
public class Channel<U, D> {
    /**
     * 管道状态事件
     */
    private final StatusEvent statusEvent;
    /**
     * 连接器
     */
    private final Connector<U, D> connector;
    /**
     * 管道记录
     */
    private final ChannelRecord channelRecord;
    /**
     * 接收数据（全局）监听器
     */
    private ReceiveListener<U> receiveListener;
    /**
     * 接收数据（Feedback）监听器
     */
    private final CopyOnWriteArrayList<FeedbackHandler<U, D>> feedbackHandlers = new CopyOnWriteArrayList<>();
    /**
     * 发送数据监听器
     */
    private SendListener<D> sendListener;

    private void addFeedbackHandler(FeedbackHandler<U, D> feedbackHandler) {
        this.feedbackHandlers.remove(feedbackHandler);
        this.feedbackHandlers.add(feedbackHandler);
    }

    public Channel(Connector<U, D> connector, ReceiveListener<U> receiveListener, SendListener<D> sendListener, StatusListener statusListener) {
        this.connector = connector;
        this.channelRecord = new ChannelRecord();
        this.receiveListener = receiveListener;
        this.sendListener = sendListener;
        this.statusEvent = new StatusEvent(contact(), Status.NOT_CONNECTION, statusListener);
    }

    /**
     * 管道状态
     *
     * @return
     */
    public Status status() {
        return this.statusEvent.status();
    }

    /**
     * 管道错误状态提示信息
     *
     * @return
     */
    public String errorMessage() {
        return this.statusEvent.errorMessage();
    }

    /**
     * 是否可用
     *
     * @return
     */
    public boolean available() {
        return Status.CONNECTION_SUCCEEDED.equals(status()) || Status.COMMUNICATION_SUCCEEDED.equals(status());
    }

    /**
     * 超时未通信
     *
     * @param milliseconds
     * @return
     */
    public boolean timeoutNoCommunication(Long milliseconds) {
        return this.channelRecord.overTimeNotReceivedFromLast(milliseconds);
    }

    /**
     * 创建管道连接
     */
    public void connect() throws ConnectorException {
        try {
            this.statusEvent.connectStarted();
            this.connector.connect();
            this.statusEvent.connectSucceed();
        } catch (Exception ex) {
            this.statusEvent.connectFailed(ex.getMessage());
            throw new ConnectorException("Connector 创建连接时失败", ex);
        }
    }

    /**
     * 获取管道连接触点
     *
     * @return
     */
    public Contact contact() {
        Contact contact = null;
        try {
            contact = this.connector.contact();
        } catch (NetPointException | ConnectorException ex) {
            ex.printStackTrace();
        }
        if (ObjectUtil.isEmpty(contact)) {
            contact = new Contact(this.connector.getLocal(), this.connector.getRemote());
        }
        return contact;
    }

    /**
     * 断开管道连接
     */
    public void disconnect() throws ConnectorException {
        try {
            this.connector.disconnect();
            this.statusEvent.disconnected();
        } catch (Exception ex) {
            SysLogger.error(ex, "Connector 断开连接时失败 ", "", "", "");
            throw new ConnectorException("Connector 断开连接时失败", ex);
        }
    }

    /**
     * 重建管道连接
     */
    public void reconnect() throws ConnectorException {
        if (this.connector.isConnected()) {
            this.disconnect();
        }
        this.connect();
    }

    /**
     * 发送数据
     *
     * @param dData
     */
    public void send(D dData) {
        if (ObjectUtil.isEmpty(dData)) {
            return;
        }
        try {
            connectorSend(dData);
            sendListener.succeed(contact(), dData);
        } catch (ConnectorException e) {
            sendListener.failed(contact(), dData, e);
        }
    }

    /**
     * feedback 发送数据后需要反馈
     *
     * @param feedbackHandler
     * @throws FeedbackException
     */
    public void feedback(FeedbackHandler<U, D> feedbackHandler) throws FeedbackException {
        if (feedbackHandler.isCompleted()) {
            D sendData = feedbackHandler.sendData();
            if (ObjectUtil.isNotEmpty(sendData)) {
                try {
                    connectorSend(sendData);
                    feedbackHandler.sendSucceed();
                    addFeedbackHandler(feedbackHandler);
                    sendListener.succeed(contact(), sendData);
                } catch (ConnectorException e) {
                    sendListener.failed(contact(), sendData, e);
                    feedbackHandler.sendFailed(e.getMessage());
                }
            }
        }
    }

    private void connectorSend(D data) throws ConnectorException {
        try {
            this.connector.send(data);
        } catch (Exception ex) {
            statusEvent.communicationFailed(ex.getMessage());
            throw new ConnectorException("Connector 发送数据时失败", ex);
        }
    }

    /**
     * 获取数据
     */
    public void receive() throws ConnectorException {
        U uData = null;
        try {
            uData = this.connector.receive();
        } catch (Exception ex) {
            statusEvent.communicationFailed(ex.getMessage());
            throw new ConnectorException("Connector 接收数据时失败", ex);
        }

        this.feedbackHandlers.forEach(item -> {
            if (item.isCompleted() && item.status().equals(FeedbackStatus.FEEDBACK_TIMEOUT)) {
                statusEvent.communicationFailed(FeedbackStatus.FEEDBACK_TIMEOUT.getMessage());
            }
        });
        receiveListener.handle(contact(), uData);
        statusEvent.communicationSucceed();
        channelRecord.accepted();
        for (FeedbackHandler<U, D> item : this.feedbackHandlers) {
            if (!item.isCompleted()) {
                item.listener(uData);
            }
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "local=" + contact().getLocal() +
                ", remote=" + contact().getRemote() +
                ", status=" + status() +
                ", channelRecord=" + channelRecord +
                '}';
    }
}
