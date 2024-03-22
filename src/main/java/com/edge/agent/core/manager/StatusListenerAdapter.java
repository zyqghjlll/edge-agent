package com.edge.agent.core.manager;

import com.edge.agent.core.manager.buffer.BufferHelper;
import com.edge.agent.core.manager.buffer.DataEvent;
import com.edge.agent.core.manager.buffer.StatusInfo;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.core.channel.Contact;
import com.edge.agent.core.channel.Status;
import com.edge.agent.core.channel.StatusListener;

import java.util.Date;

/**
 * @author zyq
 */
public class StatusListenerAdapter implements StatusListener {
    private BufferHelper bufferHelper;

    public StatusListenerAdapter(BufferHelper bufferHelper, int batchCount) {
        this.bufferHelper = bufferHelper;
    }

    @Override
    public void statusChanged(Contact contact, Status originalState, Status currentStatus, Date changedDate) {
        // 写入MindSphere
        boolean isNormal = false;
        if (Status.CONNECTION_SUCCEEDED.equals(currentStatus) || Status.COMMUNICATION_SUCCEEDED.equals(currentStatus)) {
            isNormal = true;
        }

        DataEvent dataEvent = new DataEvent();
        dataEvent.setId(CommonUtil.getId());
        dataEvent.setData(new StatusInfo(contact, isNormal));
        this.bufferHelper.publishEvent(dataEvent);
    }
}
