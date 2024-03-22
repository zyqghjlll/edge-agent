package com.edge.agent.repository.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zyq
 */
@Data
public class DbPlcPoint implements Serializable {
    @TableId
    private Long pkPoint;
    private Long pkPlc;
    private String endpointUrl;
    private String namespaceIndex;
    private String identifier;
    private String pointCode;
    private String pointDescription;
    private String pointType;
    private String defaultValue;
    private int sort;


}
