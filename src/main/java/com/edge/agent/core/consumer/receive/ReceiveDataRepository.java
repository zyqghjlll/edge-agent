package com.edge.agent.core.consumer.receive;

import com.edge.agent.repository.mysql.entity.DbReceiveRecord;

import java.util.Date;
import java.util.List;

/**
 * @author zyq
 */
public interface ReceiveDataRepository {
    /**
     * batch writing
     * @param list
     */
    void saveBatch(List<DbReceiveRecord> list);

    void remove(List<Long> pkRecordList);

    List<Long> findHistoryList(Long pkRecord, int limit);

    Long findLastOneByDate(Date date);

    List<DbReceiveRecord> findAllById(List<Long> ids);
}
