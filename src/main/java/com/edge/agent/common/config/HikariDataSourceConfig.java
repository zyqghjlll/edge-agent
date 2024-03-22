package com.edge.agent.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description：Hikari配置
 */
@Getter
@Configuration
public class HikariDataSourceConfig {
    /**
     * 配置允许连接池达到的最大连接数（包括空闲和正在使用的），当池中连接达到maximumPoolSize，且都不空闲，当有新请求从池中申请连接池会阻塞等待可用连接，达到connectionTimeout还不能申请成功，则抛出SQLException。
     * 缺省：10
     * 最佳实践：根据实际环境配置，通常设置为核心数的2倍较优
     */
    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize;
    /**
     * 这个属性控制连接返回池中前auto-commit是否自动进行。缺省：true。
     * 最佳实践：不需要配置，保持缺省即可。
     */
    @Value("${spring.datasource.hikari.auto-commit}")
    private boolean autoCommit;
    /**
     * 控制空闲连接的在池中最大的空闲时间。
     * 注意：这个配置只有当配置了minimumIdle属性(表示允许的最小空闲连接数)，且比maximumPoolSize（表示池中允许的最大连接数）更小时才生效。
     * 当前池中有空闲连接且比允许的最小空闲连接多时，根据空闲超时时间来逐出。
     * <p>
     * 当配置为0时表示空闲连接永远不逐出。
     * 缺省：600000ms
     * 最小生效值：10000ms
     * 连接池会定时轮询检测哪些连接是空闲，并且空闲达到了idleTimeout配置的时间，但轮询间隔有时间差，一个空闲连接最大可空闲idleTimeout + 30s会逐出，平均是：idleTimeout + 15s。
     * <p>
     * 最佳实践：不设置该属性和minimumIdle属性，保持连接池固定的连接
     */
    @Value("${spring.datasource.hikari.idle-timeout}")
    private int idleTimeout;
    @Value("${spring.datasource.hikari.pool-name}")
    private String poolName;
    /**
     * 该属性用于控制连接在池中的最大生存时间，超过该时间强制逐出，连接池向数据申请新的连接进行补充。注意：当前正在使用的连接不会强制逐出，哪怕它的累计时间已经到了maxLifetime。
     * 强烈建议设置该属性，可设置的比数据库或网络基础设施允许的最大连接时间小一些。 如数据库连接最大失效时间是8小时，可设置为4小时。
     * 缺省：1800000， 即30min
     * 最小可配置：30000，即30s
     * 最佳实践：需要设置，根据数据库或网络基础设施的情况，比它们小一些
     */
    @Value("${spring.datasource.hikari.max-lifetime}")
    private int maxLifetime;
    /**
     * 控制一个客户端等待从池中获取连接的最大时间。超过该时间还获取不到连接则抛出SQLException.
     * 最低可设置的时间是250ms，缺省：30000ms
     * 最佳实践：非特殊业务场景保持缺省30s连接超时即可。
     */
    @Value("${spring.datasource.hikari.connection-timeout}")
    private int connectionTimeout;
    /**
     * 如果当前连接驱动支持JDBC4, 强烈不建议设置此属性。因为该属性的设置是为了支持keepalive检测，只有当JDBC4的isValid不可用时才使用connectionTestQuery配置的sql进行检测；或者当从池中获取连接时检测连接是否有效
     * 缺省：none
     * 最佳实践：驱动支持JDBC4不需要设置，反之需要配置，MYSQL: select 1; Oracle: select 1 from dual
     */
    @Value("${spring.datasource.hikari.connection-test-query}")
    private String connectionTestQuery;

    @Autowired
    private DataSourceConfig dataSourceConfig;
    private HikariDataSource hikariDataSource;

    /**
     * 创建连接池
     *
     * @return
     */
    @Bean
    public HikariDataSource getHikariDataSource() {
        if (ObjectUtil.isEmpty(hikariDataSource)) {
            hikariDataSource = new HikariDataSource();
            hikariDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
            hikariDataSource.setJdbcUrl(dataSourceConfig.getUrl());
            hikariDataSource.setUsername(dataSourceConfig.getUserName());
            hikariDataSource.setPassword(dataSourceConfig.getPassword());
            hikariDataSource.setMaximumPoolSize(maximumPoolSize);
            hikariDataSource.setIdleTimeout(idleTimeout);
            hikariDataSource.setMaxLifetime(maxLifetime);
            hikariDataSource.setConnectionTimeout(connectionTimeout);
            hikariDataSource.setConnectionTestQuery(connectionTestQuery);
            hikariDataSource.setPoolName(poolName);
            hikariDataSource.setAutoCommit(autoCommit);
        }
        return hikariDataSource;
    }

    public HikariDataSource getSingleHikariDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        hikariDataSource.setJdbcUrl(dataSourceConfig.getUrl());
        hikariDataSource.setUsername(dataSourceConfig.getUserName());
        hikariDataSource.setPassword(dataSourceConfig.getPassword());
        hikariDataSource.setMaximumPoolSize(1);
        hikariDataSource.setIdleTimeout(idleTimeout);
        hikariDataSource.setMaxLifetime(maxLifetime);
        hikariDataSource.setConnectionTimeout(connectionTimeout);
        hikariDataSource.setConnectionTestQuery(connectionTestQuery);
        hikariDataSource.setAutoCommit(autoCommit);
        return hikariDataSource;
    }
}

