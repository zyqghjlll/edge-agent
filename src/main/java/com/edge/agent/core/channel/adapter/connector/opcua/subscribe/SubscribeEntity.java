package com.edge.agent.core.channel.adapter.connector.opcua.subscribe;

import lombok.Data;

import java.util.Date;

/**
 * @author zyq
 * @date 2022/12/6 15:16
 */
@Data
public class SubscribeEntity {
    private Long id;
    private String key;
    private String value;
    private Integer nameSpaceIndex;
    private String endpointUrl;
    private Date receivingTime;

    public SubscribeEntity() {
        this.id = 0L;
        this.key = "";
        this.value = "";
        this.nameSpaceIndex = 0;
        this.endpointUrl = "";
        this.receivingTime = null;
    }

    @Override
    public String toString() {
        return "SubscribeEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", nameSpaceIndex=" + nameSpaceIndex +
                ", endpointUrl='" + endpointUrl + '\'' +
                ", receivingTime=" + receivingTime +
                '}';
    }
}
