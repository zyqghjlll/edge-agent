package com.edge.agent.core.consumer.receive;

import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.core.channel.adapter.connector.opcua.NodeValue;
import com.edge.agent.core.manager.RawInfo;
import com.edge.agent.core.manager.buffer.AbstractEventHandler;
import com.edge.agent.core.manager.buffer.DataEvent;
import com.edge.agent.repository.mysql.entity.DbReceiveRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zyq
 */
public class ReceiveDataHandler extends AbstractEventHandler {

    private ReceiveDataRepository receiveDataRepository;

    public ReceiveDataHandler(ReceiveDataRepository receiveDataRepository) {
        super("ReceiveDataHandler");
        this.receiveDataRepository = receiveDataRepository;
    }

    @Override
    public void handler(DataEvent dataEvent) {
        List<Object> objects = new ArrayList<>();
        objects.add(dataEvent.getData());
        save(dataEvent.getData());
    }

    private void save(Object objects) {
        try {
            RawInfo<List<NodeValue>> rawInfo = (RawInfo<List<NodeValue>>) objects;
            SysLogger.debug("ReceiveDataHandler 的接收时间 receiveTime: [%s], id:[%s]", CommonUtil.getDateString(), rawInfo.getPkRecord());
            SysLogger.debug("ReceiveDataHandler 消费者开始处理 receiveTime: [%s], consumerTime: [%s], [%s]ms", rawInfo.getReceiveTime(), CommonUtil.getDate(), CommonUtil.getDate().getTime() - rawInfo.getReceiveTime().getTime());

            List<DbReceiveRecord> receiveRecords = new ArrayList<>();
            DbReceiveRecord receiveRecord = new DbReceiveRecord();
            receiveRecord.setPkRecord(rawInfo.getPkRecord());
            receiveRecord.setLocalIp(rawInfo.getContact().getLocal().getIp());
            receiveRecord.setLocalPort(rawInfo.getContact().getLocal().getPort() + "");
            receiveRecord.setRemoteIp(rawInfo.getContact().getRemote().getIp());
            receiveRecord.setRemotePort(rawInfo.getContact().getRemote().getPort() + "");
            receiveRecord.setRawType(String.valueOf(rawInfo.getRawData().get(2).getValue()));
            receiveRecord.setRawTime(rawInfo.getReceiveTime());
            List<String> valueList = rawInfo.getRawData().stream().map(m -> String.valueOf(m.getValue())).collect(Collectors.toList());
            receiveRecord.setRawData(valueList.toString());
            receiveRecord.setAnlData("");
            receiveRecord.setArrData(valueList.toString());
            receiveRecord.setIsEffective(1);
            receiveRecord.setReason("");
            receiveRecord.setReceiveTime(rawInfo.getReceiveTime());
            receiveRecord.setCreateTime(new Date());
            receiveRecords.add(receiveRecord);
            receiveDataRepository.saveBatch(receiveRecords);
        } catch (Exception ex) {
            SysLogger.error(ex, "【Mysql写入】失败");
        }
    }
}
