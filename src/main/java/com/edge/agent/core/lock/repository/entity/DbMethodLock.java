package com.edge.agent.core.lock.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zyq
 */
@Data
public class DbMethodLock implements Serializable {
    @TableId
    private int id;
    private String methodName;
    private String desc;
    private Date updateTime;

}
