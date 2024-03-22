package com.edge.agent.core.lock.repository;

import com.edge.agent.utils.JdbcUtil;
import com.edge.agent.core.lock.impl.distribute.DistributedLockRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zyq
 */
public class DistributedLockRepositoryImpl implements DistributedLockRepository {

    private Connection connection;

    private String method;

    public DistributedLockRepositoryImpl(Connection connection, String method) {
        this.connection = connection;
        this.method = method;
    }

    @Override
    public Boolean setLock(String identifier) {
        while (true) {
            if (lock(identifier)) {
                return true;
            }

        }
    }

    @Override
    public Boolean releaseLock(String identifier) {
        boolean result = false;
        if (unlock(identifier)) {
            result = true;
        }

        return result;
    }

    private boolean lock(String identifier) {
        // 开启事务
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 执行获取锁的sql
        String sql = "select * from db_method_lock where method_name = '" + this.method + "' for update;";

        ResultSet query = JdbcUtil.query(connection, sql);
        // 结果非空，加锁成功
        if (query != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean unlock(String identifier) {
        try {
            this.connection.commit();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
