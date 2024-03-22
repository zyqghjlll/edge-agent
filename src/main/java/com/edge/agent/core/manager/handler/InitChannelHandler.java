package com.edge.agent.core.manager.handler;

import com.edge.agent.core.channel.FeedbackHandler;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.utils.SysLogger;

import java.util.Arrays;

public class InitChannelHandler extends FeedbackHandler<byte[], byte[]> {

    public InitChannelHandler(String identify, NetPoint local, NetPoint remote) {
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
            SysLogger.error(e, "local:[%s], remote:[%s] 创建IN报文失败", super.getLocal(), super.getRemote());
        }
        return bytes;
    }

    @Override
    protected boolean filter(byte[] data) {
        return true;
    }

    @Override
    protected void feedbackSucceedHandler(byte[] data) {
        SysLogger.info("local:[%s], remote:[%s] 管道初始化成功, data:[%s]", super.getLocal(), super.getRemote(), Arrays.toString(data));
    }

    @Override
    protected void feedbackFailedHandler(String message) {
        SysLogger.info("local:[%s], remote:[%s] 管道初始化失败，失败原因：[%s]", super.getLocal(), super.getRemote(), message);
    }
}
