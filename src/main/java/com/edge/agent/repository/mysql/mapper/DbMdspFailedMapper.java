package com.edge.agent.repository.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.mysql.entity.DbMdspFailed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DbMdspFailedMapper extends BaseMapper<DbMdspFailed> {

    List<DbMdspFailed> findNeedReSend(@Param("agentIp") String agentIp);

    void batchReSendSucceed(@Param("ids") List<Long> ids);

    void batchInsert(@Param("list") List<DbMdspFailed> list);

    List<Long> selectCanRemove(@Param("list") List<Long> pkRecordList);

    void deleteOutReceive(@Param("list") List<Long> pkRecordList);

    void updateSendTimes(@Param("pkRecordList") List<Long> pkRecordList);

    void batchReSendFailed(@Param("ids") List<Long> ids);
}
