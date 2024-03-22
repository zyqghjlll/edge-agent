package com.edge.agent.repository.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DbMdspFailed implements Serializable {
    @TableId
    private Long pkMdspFailed;
    private Long pkRecord;
    /**
     * 0：未成功发送；1：已成功发送
     */
    private int status;
    private int sendTimes;
    private Date createTime;
}
