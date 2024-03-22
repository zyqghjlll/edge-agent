package com.edge.agent.core.plc;

import com.edge.agent.core.netpoint.NetPoint;

import java.util.List;

public interface PlcRepository {
    List<Plc> getPlcList();

    Plc getPlcNode(NetPoint netPoint);

    Plc getPlcNode(Long pkPlc);

    void batchInsert(List<Plc> list);

    void batchUpdate(List<Plc> list);

    void batchDelete(List<Plc> list);

    List<Plc> findByGroupCode(String groupCode, String protocol);

    List<PlcPoint> getPlcPointList(NetPoint netPoint);

    void deletePlcPoint(Long pkPlc);

    void insertPlcPoint(List<PlcPoint> plcPoints);

    List<Plc> getByGroupCode(String groupCode, String protocol);

}
