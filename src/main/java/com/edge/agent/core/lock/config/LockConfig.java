package com.edge.agent.core.lock.config;

import com.edge.agent.common.config.HikariDataSourceConfig;
import com.edge.agent.core.lock.impl.distribute.DistributedLock;
import com.edge.agent.core.lock.repository.DistributedLockRepositoryImpl;
import com.edge.agent.core.lock.Lockable;
import com.edge.agent.core.lock.impl.distribute.DistributedLockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zyq
 */
@Configuration
public class LockConfig {

    @Autowired
    private HikariDataSourceConfig dataSourceConfig;

    protected DistributedLockRepository distributedLockRepository(String method) {
        Connection connection = null;
        try {
            connection = dataSourceConfig.getSingleHikariDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new DistributedLockRepositoryImpl(connection, method);
    }

    @Bean
    public Lockable getLock() {
        return new DistributedLock("", distributedLockRepository("sync"));
    }
}
