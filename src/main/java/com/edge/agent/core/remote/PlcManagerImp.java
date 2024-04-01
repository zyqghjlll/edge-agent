package com.edge.agent.core.remote;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.common.config.HikariDataSourceConfig;
import com.edge.agent.core.lock.Lockable;
import com.edge.agent.core.lock.impl.distribute.DistributedLock;
import com.edge.agent.core.lock.repository.DistributedLockRepositoryImpl;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.utils.SysLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author zyq
 */
@Component
public class PlcManagerImp implements PlcManager {

    @Autowired
    private PlcRepository plcRepository;
    private PlcContextData plcContextData;
    @Autowired
    private SyncCommand syncCommand;
    @Autowired
    private HikariDataSourceConfig dataSourceConfig;

    @Override
    public Plc getPlc(NetPoint netPoint) {
        if (ObjectUtil.isEmpty(this.plcContextData)) {
            this.plcContextData = new PlcContextData(this.plcRepository);
        }
        return this.plcContextData.get(netPoint);
    }

    @Override
    public Plc getPlc(Long pkPlc) {
        return plcRepository.getPlcNode(pkPlc);
    }

    @Override
    public List<Plc> getByGroupCode(String groupCode, String protocol) {
        return plcRepository.getByGroupCode(groupCode, protocol);
    }

    @Override
    public List<PlcPoint> getPlcPointList(Plc plc) {
        return plcRepository.getPlcPointList(plc);
    }

    @Override
    public void batchInsert(List<Plc> list) {
        plcRepository.batchInsert(list);
    }

    @Override
    public void sync(List<Plc> sourceList) {
        Connection connection = getConnection();
        if (connection == null) {
            return;
        }
        DistributedLockRepositoryImpl sync = new DistributedLockRepositoryImpl(connection, "sync");
        Lockable lockable = new DistributedLock("", sync);
        lockable.withLock(() -> syncCommand.sync(sourceList), (str, ex) -> {
            if (ex == null) {
                SysLogger.info(str);
            } else {
                SysLogger.error(ex, str);
            }
        });
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSourceConfig.getSingleHikariDataSource().getConnection();
        } catch (SQLException e) {
            SysLogger.error(e, "An error occurred while attempting to establish a connection for synchronization");
        }
        return connection;
    }

    @Override
    public void syncPlcPoints(List<OpcPointVo> opcPoint) {
        Connection connection = getConnection();
        if (connection == null) {
            return;
        }
        DistributedLockRepositoryImpl sync = new DistributedLockRepositoryImpl(connection, "sync");
        Lockable lockable = new DistributedLock("", sync);
        lockable.withLock(() -> syncCommand.syncPlcPoints(opcPoint), (str, ex) -> {
            if (ex == null) {
                SysLogger.info(str);
            } else {
                SysLogger.error(ex, str);
            }
        });
    }
}
