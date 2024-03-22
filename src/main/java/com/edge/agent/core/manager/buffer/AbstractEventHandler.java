package com.edge.agent.core.manager.buffer;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.utils.SysLogger;
import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 处理事件的逻辑
 *
 * @author zyq
 */
public abstract class AbstractEventHandler implements WorkHandler<DataEvent> {
    private String name;
    private AtomicLong count = new AtomicLong(0);

    public AbstractEventHandler(String name) {
        if (ObjectUtil.isNotEmpty(name)) {
            this.name = name;
        } else {
            this.name = "EventHandler";
        }
    }

    public AbstractEventHandler() {
        this("");
    }

    @Override
    public void onEvent(DataEvent dataEvent) {
        long startTime = System.currentTimeMillis();
        if (!dataEvent.valid()) {
            SysLogger.debug("[%s] Invalid message dataEvent：[%s], message:[%s]", this.name, dataEvent.toString(), dataEvent.getErrorMessage());
            return;
        }
        SysLogger.debug("[%s] start to handle. Total number of items:[%s], data：[%s]", this.name, count.getAndIncrement(), dataEvent.toString());

        try {
            handler(dataEvent);
        } catch (Exception ex) {
            SysLogger.error(ex, "[%s] An exception occurred while processing. ", this.name);
        } finally {
            long endTime = System.currentTimeMillis();
            SysLogger.debug("[%s] Completed. Processing time：[%s]ms", this.name, (endTime - startTime));
        }
    }

    public abstract void handler(DataEvent dataEvent);
}
