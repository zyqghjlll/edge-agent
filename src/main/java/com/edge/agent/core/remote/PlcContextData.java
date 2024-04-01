package com.edge.agent.core.remote;

import com.edge.agent.common.ContextData;
import com.edge.agent.core.netpoint.NetPoint;

import java.util.HashMap;

public class PlcContextData extends ContextData<NetPoint, Plc> {

    private PlcRepository plcRepository;

    public PlcContextData(PlcRepository plcRepository) {
        this.plcRepository = plcRepository;
    }

    @Override
    public HashMap<NetPoint, Plc> load() {
        return null;
    }

    @Override
    public Plc load(NetPoint netPoint) {
        return plcRepository.getPlcNode(netPoint);
    }
}
