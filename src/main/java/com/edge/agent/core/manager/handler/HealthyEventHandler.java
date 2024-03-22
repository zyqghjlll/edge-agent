package com.edge.agent.core.manager.handler;

import com.edge.agent.core.channel.FeedbackHandler;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.utils.SysLogger;

/**
 * @author zyq
 */
public class HealthyEventHandler extends FeedbackHandler<byte[], byte[]> {

    public HealthyEventHandler(String identify, NetPoint local, NetPoint remote) {
        super(identify, local, remote);
    }

    @Override
    protected Long timeout() {
        return 60 * 1000L;
    }

    @Override
    protected byte[] sendData() {
        byte[] bytes = new byte[0];
        try {
            bytes = null;
        } catch (Exception e) {
            SysLogger.error(e, "local:[%s], remote:[%s] 创建PI报文失败", super.getLocal(), super.getRemote());
        }
        return bytes;
    }

    @Override
    protected boolean filter(byte[] data) {
        boolean result = false;
        try {
            SysLogger.info("local:[%s], remote:[%s] PQ报文：[%s]", super.getLocal(), super.getRemote(), null);
        } catch (Exception e) {
            SysLogger.error(e, "local:[%s], remote:[%s] 等待PQ反馈报文过程中-解析报文失败", super.getLocal(), super.getRemote());
        }

        return result;
    }

    @Override
    protected void feedbackSucceedHandler(byte[] data) {
        SysLogger.info("local:[%s], remote:[%s] 心跳检测成功", super.getLocal(), super.getRemote());
    }

    @Override
    protected void feedbackFailedHandler(String message) {
        SysLogger.info("local:[%s], remote:[%s] 心跳检测失败，失败原因：[%s]", super.getLocal(), super.getRemote(), message);
    }
}
