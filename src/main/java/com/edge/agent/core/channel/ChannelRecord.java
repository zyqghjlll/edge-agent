package com.edge.agent.core.channel;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.utils.CommonUtil;
import lombok.Getter;

import java.util.Date;

@Getter
public class ChannelRecord {

    private Date connectedTime = null;
    private Date disconnectedTime = null;
    /**
     * 接收数据时间
     */
    private Date acceptedDataTime = null;
    private int acceptDataTimes = 0;
    private int reconnectedTimes = 0;

    /**
     * 已连接
     *
     * @return
     */
    protected void connected() {
        this.connectedTime = CommonUtil.getDate();
    }

    /**
     * 断开连接
     *
     * @return
     */
    protected void disconnected() {
        this.disconnectedTime = CommonUtil.getDate();
    }

    /**
     * 接收数据
     */
    protected void accepted() {
        this.acceptedDataTime = CommonUtil.getDate();
        this.acceptDataTimes++;
    }

    /**
     * 已接收次数
     *
     * @return
     */
    public int acceptedTimes() {
        return this.acceptDataTimes;
    }

    /**
     * 从首次连接以来超时未收到任何数据
     *
     * @param second
     * @return
     */
    public boolean overTimeNotReceivedFromConnected(int second) {
        boolean result = false;
        if (ObjectUtil.isNotEmpty(this.connectedTime) && System.currentTimeMillis() - this.connectedTime.getTime() > second) {
            result = true;
        }
        return result;
    }

    /**
     * 从上一次接收数据以来超时未收到任何数据
     *
     * @param milliseconds
     * @return
     */
    public boolean overTimeNotReceivedFromLast(Long milliseconds) {
        boolean result = false;
        if (ObjectUtil.isEmpty(this.acceptedDataTime) || System.currentTimeMillis() - this.acceptedDataTime.getTime() > milliseconds) {
            result = true;
        }
        return result;
    }

    @Override
    public String toString() {
        return "ChannelRecord{" +
                "connectedTime=" + connectedTime +
                ", disconnectedTime=" + disconnectedTime +
                ", acceptedDataTime=" + acceptedDataTime +
                ", acceptDataTimes=" + acceptDataTimes +
                ", reconnectedTimes=" + reconnectedTimes +
                '}';
    }
}
