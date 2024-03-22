package com.edge.agent.core.consumer.status;

import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.manager.ChannelInstanceRepository;
import com.edge.agent.core.manager.buffer.AbstractEventHandler;
import com.edge.agent.core.manager.buffer.DataEvent;
import com.edge.agent.core.manager.buffer.StatusInfo;
import com.edge.agent.core.plc.Plc;
import com.edge.agent.core.plc.PlcManager;

/**
 * @author zyq
 */
public class StatusHandler extends AbstractEventHandler {
    private PlcManager plcManager;
    private ChannelInstanceRepository channelInstanceRepository;

    public StatusHandler(PlcManager plcManager, ChannelInstanceRepository channelInstanceRepository) {
        super("StatusHandler");
        this.plcManager = plcManager;
        this.channelInstanceRepository = channelInstanceRepository;
    }

    @Override
    public void handler(DataEvent dataEvent) {
        save(dataEvent);
    }

    private void save(DataEvent dataEvent) {
        StatusInfo statusInfo = (StatusInfo) dataEvent.getData();
        String message = "|status:" + statusInfo.getStatus() + "|time:" + CommonUtil.getDate();
        Plc plc = plcManager.getPlc(statusInfo.getContact().getRemote());
        try {
            SysLogger.debug("调用mindsphere通知PLC连接状态, plc:[%s], message:[%s]", plc, message);
            message += "|syncStatus:succeed|";
        } catch (Exception e) {
            message += "|syncStatus:failed" + "|message:" + e.getMessage() + "|";
        } finally {
            SysLogger.debug("调用mindsphere通知PLC连接状态，结果, plc:[%s], message:[%s]", plc, message);
            channelInstanceRepository.syncInfo(plc, message);
        }
    }
}
