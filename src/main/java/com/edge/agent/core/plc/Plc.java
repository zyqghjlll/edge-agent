package com.edge.agent.core.plc;

import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import lombok.Getter;

import java.util.Objects;

/**
 * @author zyq
 */
@Getter
public class Plc extends NetPoint {
    private Long id;
    private String assetId;
    private String location;
    private String productionLine;
    private String farmId;
    private String groupCode;
    // 采集频率
    private Long msgCycle;
    // plc协议
    private String protocol;
    /**
     * 0：未删除；1：已删除
     */
    private int deleted = 0;

    public Plc(Long id, String ip, String port) throws NetPointException {
        super(ip, port);
        this.id = id;
    }

    public Plc(Long id, String ip, String port, String code, String description) throws NetPointException {
        super(ip, port, code, description);
        this.id = id;
    }

    public Plc setAssetId(String assetId) {
        this.assetId = assetId;
        return this;
    }

    public Plc setLocation(String location) {
        this.location = location;
        return this;
    }

    public Plc setProductionLine(String productionLine) {
        this.productionLine = productionLine;
        return this;
    }

    public Plc setFarmId(String farmId) {
        this.farmId = farmId;
        return this;
    }

    public Plc setGroupCode(String groupCode) {
        this.groupCode = groupCode;
        return this;
    }

    public Plc setDeleted(int deleted) {
        this.deleted = deleted;
        return this;
    }
    public Plc setMsgCycle(Long msgCycle) {
        this.msgCycle = msgCycle;
        return this;
    }

    public Plc setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }


    @Override
    public String toString() {
        return "Plc{" +
                "id=" + id +
                ", assetId='" + assetId + '\'' +
                ", location='" + location + '\'' +
                ", productionLine='" + productionLine + '\'' +
                ", farmId='" + farmId + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", msgCycle=" + msgCycle +
                ", protocol='" + protocol + '\'' +
                ", deleted=" + deleted +
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
        if (!super.equals(o)) {
            return false;
        }
        Plc plc = (Plc) o;
        return id.equals(plc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    public boolean isDeleted() {
        return this.deleted == 1;
    }
}
