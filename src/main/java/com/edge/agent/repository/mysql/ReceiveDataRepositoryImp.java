package com.edge.agent.repository.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.common.config.HikariDataSourceConfig;
import com.edge.agent.core.consumer.receive.ReceiveDataRepository;
import com.edge.agent.repository.mysql.entity.DbReceiveRecord;
import com.edge.agent.repository.mysql.mapper.DbReceiveRecordMapper;
import com.edge.agent.utils.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zyq
 */
@Repository
public class ReceiveDataRepositoryImp implements ReceiveDataRepository {

    @Autowired
    private DbReceiveRecordMapper repository;
    @Autowired
    private HikariDataSourceConfig dataSourceConfig;
    private Connection connectionStore;

    @Override
    public void saveBatch(List<DbReceiveRecord> list) {
        batch(list);
    }

    private void batch(List<DbReceiveRecord> receiveRecords) {
        try {
            if (CollUtil.isEmpty(receiveRecords)) {
                return;
            }

            String sql = "INSERT INTO db_receive_record "
                    + "(pk_record, local_ip, local_port, remote_ip, remote_port, raw_type, raw_time, raw_data, anl_data, " +
                    "arr_data, is_effective, reason, receive_time, create_time)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            Map<Integer, List<Object>> data = new LinkedHashMap<>();
            receiveRecords.forEach(receiveRecord -> {
                List<Object> props = new ArrayList<>();
                props.add(receiveRecord.getPkRecord());
                props.add(receiveRecord.getLocalIp());
                props.add(receiveRecord.getLocalPort());
                props.add(receiveRecord.getRemoteIp());
                props.add(receiveRecord.getRemotePort());
                props.add(receiveRecord.getRawType());
                props.add(receiveRecord.getRawTime());
                props.add(receiveRecord.getRawData());
                props.add(receiveRecord.getAnlData());
                props.add(receiveRecord.getArrData());
                props.add(receiveRecord.getIsEffective());
                props.add(receiveRecord.getReason());
                props.add(receiveRecord.getReceiveTime());
                props.add(receiveRecord.getCreateTime());
                data.put(1, props);
            });

            Connection connection = getConnection();
            JdbcUtil.insertBatch(connection, sql, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        if (ObjectUtil.isEmpty(this.connectionStore)) {
            try {
                this.connectionStore = dataSourceConfig.getHikariDataSource().getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this.connectionStore;
    }

    @Override
    public void remove(List<Long> pkRecordList) {
        repository.deleteByPkRecordList(pkRecordList);
    }

    @Override
    public List<Long> findHistoryList(Long pkRecord, int limit) {
        List<Long> result = new ArrayList<>();
        List<DbReceiveRecord> dbReceiveRecords = repository.selectHistoryList(pkRecord, limit);
        if (CollUtil.isNotEmpty(dbReceiveRecords)) {
            result = dbReceiveRecords.stream().map(DbReceiveRecord::getPkRecord).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public Long findLastOneByDate(Date date) {
        DbReceiveRecord dbReceiveRecord = repository.selectLastOneByDate(date);
        if (ObjectUtil.isEmpty(dbReceiveRecord)) {
            return null;
        }
        return dbReceiveRecord.getPkRecord();
    }

    @Override
    public List<DbReceiveRecord> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }
}
