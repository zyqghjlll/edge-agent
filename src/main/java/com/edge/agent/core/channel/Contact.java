package com.edge.agent.core.channel;

import com.edge.agent.core.netpoint.NetPoint;
import lombok.Getter;

/**
 * 管道连接的触点
 * @author zyq
 */
@Getter
public class Contact {
    /**
     * 本地连接触点
     */
    private NetPoint local;
    /**
     * 远程连接触点
     */
    private NetPoint remote;

    public Contact(NetPoint local, NetPoint remote) {
        this.local = local;
        this.remote = remote;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "local=" + local.toString() +
                ", remote=" + remote.toString() +
                '}';
    }
}
