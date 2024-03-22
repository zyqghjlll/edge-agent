package com.edge.agent.core.manager.buffer;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.utils.SysLogger;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author zyq
 */
public class BufferHelper {
    private Disruptor<DataEvent> disruptor;
    private RingBuffer<DataEvent> ringBuffer;

    public BufferHelper(int bufferSize, WorkHandler[]... consumers) {
        createBuffer(bufferSize, consumers);
    }

    /**
     * 加入缓冲队列。
     */
    public void publishEvent(DataEvent dataEvent) {
        long sequence = ringBuffer.next();
        try {
            DataEvent event = ringBuffer.get(sequence);
            event.setId(dataEvent.getId());
            event.setData(dataEvent.getData());
            ringBuffer.publish(sequence);
            Thread.currentThread().sleep(5);
        } catch (Exception ex) {
            SysLogger.error(ex, "【加入内存队列】 失败 dataEvent:[%s] ", dataEvent.toString());
        }
    }

    private void createBuffer(int bufferSize, WorkHandler[]... consumers) {
        ThreadFactory producerFactory = Executors.defaultThreadFactory();
        // 创建缓冲池
        DataEventFactory eventFactory = new DataEventFactory();
        this.disruptor = new Disruptor<>(eventFactory, bufferSize, producerFactory,
                ProducerType.SINGLE, new BlockingWaitStrategy());
        this.ringBuffer = disruptor.getRingBuffer();

        if (consumers.length > 0) {
            for (WorkHandler[] f : consumers) {
                if (ObjectUtil.isNotEmpty(f)) {
                    disruptor.handleEventsWithWorkerPool(f);
                }
            }
        }
        disruptor.start();
    }
}
