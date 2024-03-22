package com.edge.agent.repository.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.mysql.entity.DbAgent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyq
 */
public interface DbAgentMapper extends BaseMapper<DbAgent> {
    DbAgent findByCode(@Param("code") String code);

    DbAgent findByIp(@Param("ip") String ip);

    List<DbAgent> findAll();

    List<DbAgent> findByGroupCode(@Param("groupCode") String groupCode, @Param("protocol") String protocol);

    DbAgent findByServer(@Param("ip") String ip, @Param("port") int port);
}
