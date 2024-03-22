package com.edge.agent.core.netpoint;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.netpoint.exception.NetPointException;
import lombok.Getter;

import java.util.Objects;

/**
 * @author zyq
 */
@Getter
public class NetPoint {
    private String ip;
    private Integer port;
    private String code;
    private String description;

    public NetPoint(String ip, String port) throws NetPointException {
        init(ip, port, "", "");
    }

    public NetPoint(String ip, String port, String code, String description) throws NetPointException {
        init(ip, port, code, description);
    }

    private NetPoint init(String ip, String port, String code, String description) throws NetPointException {
        if (ObjectUtil.isEmpty(ip)) {
            throw new NetPointException("NetPoint的IP不能为空 NetPoint:");
        }
        this.ip = ip;
        try {
            this.port = Integer.parseInt(port);
        } catch (Exception ex) {
            this.port = null;
        }
        this.code = code;
        this.description = description;
        return this;
    }

    public boolean same(NetPoint netPoint) {
        boolean result = false;
        if (this.ip.equals(netPoint.ip) && this.port.equals(netPoint.port)) {
            result = true;
        }
        return result;
    }

    public NetPoint setCode(String code) {
        this.code = code;
        return this;
    }

    public NetPoint setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return "NetPoint{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetPoint netPoint = (NetPoint) o;
        boolean result = false;
        if (ip.equals(netPoint.ip)) {
            if ((ObjectUtil.isNotEmpty(port) && port.equals(netPoint.port)) || (ObjectUtil.isEmpty(port) && ObjectUtil.isEmpty(netPoint.port))) {
                result = true;
            }
        }
        return result;
    }
}
