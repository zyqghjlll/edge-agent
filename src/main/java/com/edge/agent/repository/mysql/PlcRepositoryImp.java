package com.edge.agent.repository.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.core.remote.Plc;
import com.edge.agent.core.remote.PlcPoint;
import com.edge.agent.core.remote.PlcRepository;
import com.edge.agent.repository.mysql.entity.DbPlc;
import com.edge.agent.repository.mysql.entity.DbPlcPoint;
import com.edge.agent.repository.mysql.mapper.DbPlcMapper;
import com.edge.agent.repository.mysql.mapper.DbPlcPointMapper;
import com.edge.agent.utils.SysLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class PlcRepositoryImp implements PlcRepository {

    @Autowired
    private DbPlcMapper dbPlcMapper;
    @Autowired
    private DbPlcPointMapper dbPlcPointMapper;

    @Override
    public List<Plc> getPlcList() {
        List<Plc> result = new ArrayList<>();
        List<DbPlc> dbPlcs = dbPlcMapper.findAll();
        if (CollUtil.isNotEmpty(dbPlcs)) {
            dbPlcs.forEach(item -> {
                Plc plc = null;
                try {
                    plc = new Plc(item.getPkPlc(), item.getIp(), item.getPort(), item.getCode(), item.getDescription());
                    plc.setAssetId(item.getAssetId());
                    plc.setLocation(item.getLocation());
                    plc.setProductionLine(item.getProductionLine());
                    plc.setDeleted(item.getDeleted());
                    plc.setProtocol(item.getProtocol());
                    plc.setMsgCycle(item.getMsgCycle());
                    result.add(plc);
                } catch (NetPointException e) {
                    SysLogger.error(e, "获取PLC配置过程失败。");
                }

            });
        }
        return result;
    }

    @Override
    public Plc getPlcNode(NetPoint netPoint) {
        Plc result = null;
        DbPlc byIpAndPort = dbPlcMapper.findByIpAndPort(netPoint.getIp(), netPoint.getPort() + "");
        if (ObjectUtil.isNotEmpty(byIpAndPort)) {
            try {
                result = byIpAndPort.to();
            } catch (NetPointException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Plc getPlcNode(Long pkPlc) {
        Plc result = null;
        DbPlc dbPlc = dbPlcMapper.selectById(pkPlc);
        try {
            if (ObjectUtil.isNotEmpty(dbPlc)) {
                result = dbPlc.to();
            }
        } catch (NetPointException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void batchInsert(List<Plc> list) {
        List<DbPlc> pls = trans(list);
        dbPlcMapper.batchInsert(pls);
    }

    @Override
    public void batchUpdate(List<Plc> list) {
        List<DbPlc> pls = trans(list);
        dbPlcMapper.batchUpdate(pls);
    }

    @Override
    public void batchDelete(List<Plc> list) {
        List<DbPlc> pls = trans(list);
        pls.stream().forEach(item -> item.setDeleted(1));
        dbPlcMapper.batchUpdate(pls);
    }

    /**
     * 根据车间id，查询所属plc
     *
     * @param groupCode
     * @return
     */
    @Override
    public List<Plc> findByGroupCode(String groupCode, String protocol) {
        List<Plc> result = new ArrayList<>();
        List<DbPlc> dbPlcList = dbPlcMapper.findByGroupCode(groupCode, protocol);
        if (CollUtil.isNotEmpty(dbPlcList)) {
            dbPlcList.forEach(item -> {
                try {
                    Plc plc = item.to();
                    result.add(plc);
                } catch (NetPointException e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    @Override
    public List<PlcPoint> getPlcPointList(NetPoint netPoint) {
        List<PlcPoint> result = new ArrayList<>();
        Plc plcNode = getPlcNode(netPoint);
        if (ObjectUtil.isNotEmpty(plcNode)) {
            LambdaQueryWrapper<DbPlcPoint> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DbPlcPoint::getPkPlc, plcNode.getId());
            List<DbPlcPoint> dbPlcPoints = dbPlcPointMapper.selectList(wrapper);
            for (DbPlcPoint dbPlcPoint : dbPlcPoints) {
                PlcPoint plcPoint = new PlcPoint();
                plcPoint.setId(dbPlcPoint.getPkPoint());
                plcPoint.setPkPlc(dbPlcPoint.getPkPlc());
                plcPoint.setEndpointUrl(dbPlcPoint.getEndpointUrl());
                plcPoint.setNamespaceIndex(dbPlcPoint.getNamespaceIndex());
                plcPoint.setIdentifier(dbPlcPoint.getIdentifier());
                plcPoint.setPointType(dbPlcPoint.getPointType());
                plcPoint.setSort(dbPlcPoint.getSort());
                result.add(plcPoint);
            }
        }

        return result;
    }

    @Override
    public void deletePlcPoint(Long pkPlc) {
        LambdaQueryWrapper<DbPlcPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DbPlcPoint::getPkPlc, pkPlc);
        dbPlcPointMapper.delete(wrapper);
    }

    @Override
    public void insertPlcPoint(List<PlcPoint> plcPoints) {
        plcPoints.forEach(plcPoint -> {
            DbPlcPoint dbPlcPoint = new DbPlcPoint();
            dbPlcPoint.setPkPoint(plcPoint.getId());
            dbPlcPoint.setPkPlc(plcPoint.getPkPlc());
            dbPlcPoint.setEndpointUrl(plcPoint.getEndpointUrl());
            dbPlcPoint.setNamespaceIndex(plcPoint.getNamespaceIndex());
            dbPlcPoint.setIdentifier(plcPoint.getIdentifier());
            dbPlcPoint.setPointCode("");
            dbPlcPoint.setPointDescription("");
            dbPlcPoint.setPointType(plcPoint.getPointType());
            dbPlcPoint.setDefaultValue("");
            dbPlcPoint.setSort(plcPoint.getSort());
            dbPlcPointMapper.insert(dbPlcPoint);
        });
    }

    /**
     * 根据车间id，查询所属plc
     *
     * @param groupCode
     * @return
     */
    @Override
    public List<Plc> getByGroupCode(String groupCode, String protocol) {
        List<Plc> result = new ArrayList<>();
        List<DbPlc> dbPlcList = dbPlcMapper.findByGroupCode(groupCode, protocol);
        if (CollUtil.isNotEmpty(dbPlcList)) {
            dbPlcList.forEach(item -> {
                try {
                    Plc plc = item.to();
                    result.add(plc);
                } catch (NetPointException e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    private List<DbPlc> trans(List<Plc> list) {
        List<DbPlc> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                DbPlc dbPlc = new DbPlc();
                dbPlc.setPkPlc(item.getId());
                dbPlc.setAssetId(item.getAssetId());
                dbPlc.setIp(item.getIp());
                dbPlc.setPort(item.getPort() + "");
                dbPlc.setCode(item.getCode());
                dbPlc.setDescription(item.getDescription());
                dbPlc.setLocation(item.getLocation());
                dbPlc.setProductionLine(item.getProductionLine());
                dbPlc.setFarmId(item.getFarmId());
                dbPlc.setGroupCode(item.getGroupCode());
                dbPlc.setDeleted(item.getDeleted());
                dbPlc.setSyncTime(new Date());
                dbPlc.setProtocol(item.getProtocol());
                dbPlc.setMsgCycle(item.getMsgCycle());
                result.add(dbPlc);
            });
        }
        return result;
    }
}
