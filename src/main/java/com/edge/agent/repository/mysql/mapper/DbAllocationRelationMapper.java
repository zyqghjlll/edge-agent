package com.edge.agent.repository.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.mysql.entity.DbAllocationRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyq
 */
public interface DbAllocationRelationMapper extends BaseMapper<DbAllocationRelation> {

    List<DbAllocationRelation> findByAgents(@Param("pkAgents") List<Long> pkAgents);

    List<DbAllocationRelation> findByAgent(@Param("pkAgent") Long pkAgent);

    DbAllocationRelation findByAgentAndPlc(@Param("pkAgent") Long pkAgent, @Param("pkPlc") Long pkPlc);

    void deleteByPlc(@Param("plcIds") List<Long> plcIds);
}
