package com.edge.agent.core.manager.buffer;

import com.lmax.disruptor.EventFactory;

public class DataEventFactory implements EventFactory<DataEvent> {
    @Override
    public DataEvent newInstance() {
        return new DataEvent();
    }
}
