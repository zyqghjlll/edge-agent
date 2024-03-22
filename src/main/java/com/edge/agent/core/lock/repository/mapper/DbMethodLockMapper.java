package com.edge.agent.core.lock.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edge.agent.repository.mysql.entity.DbAgent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyq
 */
public interface DbMethodLockMapper extends BaseMapper<DbAgent> {
    DbAgent findByCode(@Param("code") String code);

    DbAgent findByIp(@Param("ip") String ip);

    List<DbAgent> findAll();

    List<DbAgent> findByGroupCode(@Param("groupCode") String groupCode);

    DbAgent findByServer(@Param("ip") String ip, @Param("port") int port);
}
