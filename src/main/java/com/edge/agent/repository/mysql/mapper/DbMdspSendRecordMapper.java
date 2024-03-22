package com.edge.agent.repository.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.mysql.entity.DbMdspSendRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DbMdspSendRecordMapper extends BaseMapper<DbMdspSendRecord> {
    void batchInsert(@Param("sendTarget") String sendTarget, @Param("list") List<DbMdspSendRecord> sendRecords);

    void deleteOutReceive(List<Long> pkRecordList);
}
