package com.edge.agent.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DbMdspSendRecord implements Serializable {
    @TableId
    private Long pkMdspSendRecord;
    private Long pkRecord;
    private String sendTarget;
    private int whetherFirstTimeToSend;
    private int sendStatus;
    private String failedReason;
    private Date receiveTime;
    private Date sendTime;
    private Date completeTime;
    private Date createTime;
}
