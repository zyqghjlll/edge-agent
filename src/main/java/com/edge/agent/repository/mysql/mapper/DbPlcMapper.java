package com.edge.agent.repository.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.mysql.entity.DbPlc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyq
 */
public interface DbPlcMapper extends BaseMapper<DbPlc> {
    DbPlc findByIpAndPort(@Param("ip") String ip, @Param("port") String port);

    List<DbPlc> findAll();

    List<DbPlc> findUnDeleted();

    void batchInsert(@Param("list") List<DbPlc> list);

    void batchUpdate(@Param("list") List<DbPlc> list);

    List<DbPlc> findByGroupCode(@Param("groupCode") String groupCode, @Param("protocol") String protocol);
}
