package com.edge.agent.repository.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.repository.mysql.entity.DbChannelInstance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DbChannelInstanceMapper extends BaseMapper<DbChannelInstance> {

    List<DbChannelInstance> findByAgent(@Param("agentIp") String agentIp);

    DbChannelInstance findByNetPoint(@Param("agent") NetPoint agent, @Param("plc") NetPoint plc);

    List<DbChannelInstance> findByNetPointAll(@Param("agent") NetPoint agent, @Param("plc") NetPoint plc);

    List<DbChannelInstance> findByRelation(@Param("pkAllocationRelation") Long pkAllocationRelation);

    void deleteByRelation(@Param("pkAllocationRelation") Long pkAllocationRelation);

    void deleteByRelations(@Param("relationIds") List<Long> relationIds);

    void updateSyncInfo(String ip, Integer port, String message);
}
