package com.edge.agent.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DbReceiveRecord implements Serializable {
    @TableId
    private Long pkRecord;
    private String localIp;
    private String localPort;
    private String remoteIp;
    private String remotePort;
    private String rawType;
    private Date rawTime;
    private String rawData;
    private String anlData;
    private String arrData;
    private int isEffective;
    private String reason;
    private Date receiveTime;
    private Date createTime;
}
