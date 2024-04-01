package com.edge.agent.repository.mysql.entity;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.core.remote.Plc;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zyq
 */
@Data
public class DbPlc implements Serializable {
    @TableId
    private Long pkPlc;
    private String assetId;
    private String ip;
    private String port;
    private String code;
    private String description;
    private String location;
    private String productionLine;
    private String farmId;
    private String groupCode;
    private int deleted;
    private Date syncTime;
    //采集频率
    private Long msgCycle;
    //plc协议
    private String protocol;

    public Plc to() throws NetPointException {
        if (ObjectUtil.isEmpty(this)) {
            return null;
        }
        Plc plc = new Plc(this.getPkPlc(), this.getIp(), this.getPort(), this.getCode(), this.getDescription());
        plc.setAssetId(this.getAssetId());
        plc.setLocation(this.getLocation());
        plc.setProductionLine(this.getProductionLine());
        plc.setFarmId(this.farmId);
        plc.setGroupCode(this.groupCode);
        plc.setDeleted(this.getDeleted());
        plc.setProtocol(this.getProtocol());
        plc.setMsgCycle(this.getMsgCycle());
        return plc;
    }

    public DbPlc from(Plc plc) {
        DbPlc dbPlc = new DbPlc();
        if (ObjectUtil.isEmpty(this)) {
            return dbPlc;
        }
        dbPlc.setPkPlc(plc.getId());
        dbPlc.setAssetId(plc.getAssetId());
        dbPlc.setIp(plc.getIp());
        dbPlc.setPort(plc.getPort() + "");
        dbPlc.setCode(plc.getCode());
        dbPlc.setDescription(plc.getDescription());
        dbPlc.setLocation(plc.getLocation());
        dbPlc.setProductionLine(plc.getProductionLine());
        dbPlc.setFarmId(plc.getFarmId());
        dbPlc.setGroupCode(plc.getGroupCode());
        dbPlc.setDeleted(plc.getDeleted());
        dbPlc.setSyncTime(new Date());
        dbPlc.setMsgCycle(plc.getMsgCycle());
        dbPlc.setProtocol(plc.getProtocol());
        return dbPlc;
    }
}
