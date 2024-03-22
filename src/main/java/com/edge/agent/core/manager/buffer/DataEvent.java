package com.edge.agent.core.manager.buffer;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author zyq
 */
@Data
public class DataEvent {
    private Long id;
    private Object data;

    private Date pushTime;

    public boolean valid() {
        return ObjectUtil.isEmpty(getErrorMessage());
    }

    public String getErrorMessage() {
        String result = "";
        if (ObjectUtil.isEmpty(id)) {
            result = "DataEvent:id 不能为空";
        }
        if (ObjectUtil.isEmpty(data)) {
            result = "DataEvent:data 不能为空";
        }
        return result;
    }

    @Override
    public String toString() {
        return "DataEvent{" +
                "id=" + id +
                ", data=" + data +
                ", pushTime=" + pushTime +
                '}';
    }
}

