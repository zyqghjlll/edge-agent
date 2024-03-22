package com.edge.agent.core.plc;

import com.edge.agent.core.netpoint.NetPoint;

import java.util.List;

public interface PlcManager {
    Plc getPlc(NetPoint netPoint);

    Plc getPlc(Long pkPlc);

    List<Plc> getByGroupCode(String groupCode, String protocol);

    List<PlcPoint> getPlcPointList(Plc plc);

    void batchInsert(List<Plc> list);

    void sync(List<Plc> sourceList);

    void syncPlcPoints(List<OpcPointVo> opcPoint);
}
