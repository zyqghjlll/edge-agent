package com.edge.agent.core.manager;

import com.edge.agent.core.channel.Connector;
import com.edge.agent.core.channel.ReceiveListener;
import com.edge.agent.core.channel.SendListener;
import com.edge.agent.core.channel.StatusListener;
import com.edge.agent.core.channel.*;
import lombok.Getter;

import java.util.Objects;

/**
 * @author zyq
 */
@Getter
public class ChannelConfig<U, D> {
    private Long pkRelation;
    private Connector<U, D> connector;
    private ReceiveListener<U> receiveListener;
    private SendListener<D> sendListener;
    private StatusListener statusListener;
    private Long msgCycle;

    public ChannelConfig(Long pkRelation, Connector<U, D> connector) {
        this.pkRelation = pkRelation;
        this.connector = connector;
    }

    public ChannelConfig<U, D> setReceiveListener(ReceiveListener<U> receiveListener) {
        this.receiveListener = receiveListener;
        return this;
    }

    public ChannelConfig<U, D> setSendListener(SendListener<D> sendListener) {
        this.sendListener = sendListener;
        return this;
    }

    public ChannelConfig<U, D> setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
        return this;
    }

    public ChannelConfig<U, D> setMsgCycle(Long msgCycle) {
        this.msgCycle = msgCycle;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelConfig<U, D> that = (ChannelConfig<U, D>) o;
        return pkRelation.equals(that.pkRelation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkRelation);
    }
}
