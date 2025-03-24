package com.edge.agent.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.entity.DbReceiveRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface DbReceiveRecordMapper extends BaseMapper<DbReceiveRecord> {
    List<DbReceiveRecord> findAllById(@Param("ids") List<Long> ids);

    void saveAll(@Param("list") List<DbReceiveRecord> receiveRecords);

    List<DbReceiveRecord> selectHistoryList(@Param("pkRecord") Long pkRecord, @Param("limit") int limit);

    DbReceiveRecord selectLastOneByDate(@Param("date") Date date);

    void deleteByPkRecordList(@Param("pkRecordList") List<Long> pkRecordList);
}
