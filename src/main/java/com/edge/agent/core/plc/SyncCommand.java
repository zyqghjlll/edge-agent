package com.edge.agent.core.plc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zyq
 */
@Component
class SyncCommand {
    @Autowired
    private PlcRepository plcRepository;

    private List<Plc> batchUpdate;
    private List<Plc> batchDelete;
    private List<Plc> batchInsert;

    public void sync(List<Plc> sourceList) {
        batchUpdate = new ArrayList<>();
        batchDelete = new ArrayList<>();
        batchInsert = new ArrayList<>();

        // 获得PLC配置
        if (CollUtil.isEmpty(sourceList)) {
            this.batchDelete.addAll(sourceList);
            exec();
            return;
        }

        List<Plc> currentList = plcRepository.getPlcList();
        if (ObjectUtil.isEmpty(currentList)) {
            this.batchInsert.addAll(sourceList);
            exec();
            return;
        }

        for (Plc plc : currentList) {
            Optional<Plc> any = sourceList.stream().filter(f -> f.same(plc)).findAny();
            if (!any.isPresent()) {
                // 删除
                this.batchDelete.add(plc);
            }
        }

        for (Plc item : sourceList) {
            Optional<Plc> any = currentList.stream().filter(f -> f.same(item)).findAny();
            if (any.isPresent()) {
                // 更新
                any.get().setAssetId(item.getAssetId());
                any.get().setCode(item.getCode());
                any.get().setDescription(item.getDescription());
                any.get().setLocation(item.getLocation());
                any.get().setProductionLine(item.getProductionLine());
                any.get().setFarmId(item.getFarmId());
                any.get().setGroupCode(item.getGroupCode());
                any.get().setDeleted(0);
                any.get().setMsgCycle(item.getMsgCycle());
                any.get().setProtocol(item.getProtocol());
                this.batchUpdate.add(any.get());
            } else {
                // 新增
                try {
                    Plc node = new Plc(CommonUtil.getId(), item.getIp(), item.getPort() + "");
                    node.setAssetId(item.getAssetId());
                    node.setCode(item.getCode());
                    node.setDescription(item.getDescription());
                    node.setLocation(item.getLocation());
                    node.setProductionLine(item.getProductionLine());
                    node.setFarmId(item.getFarmId());
                    node.setGroupCode(item.getGroupCode());
                    node.setMsgCycle(item.getMsgCycle());
                    node.setProtocol(item.getProtocol());
                    this.batchInsert.add(node);
                } catch (NetPointException e) {
                    e.printStackTrace();
                }

            }
        }

        exec();
    }

    private void exec() {
        if (CollUtil.isNotEmpty(batchUpdate)) {
            plcRepository.batchUpdate(batchUpdate);
        }
        if (CollUtil.isNotEmpty(batchDelete)) {
            plcRepository.batchDelete(batchDelete);
        }
        if (CollUtil.isNotEmpty(batchInsert)) {
            plcRepository.batchInsert(batchInsert);
        }
    }

    public void syncPlcPoints(List<OpcPointVo> opcPoint) {
        if (CollUtil.isEmpty(opcPoint)) {
            SysLogger.info("同步PLC points 时，数据来源为空");
            return;
        }

        List<Plc> opcList = plcRepository.getPlcList();
        if (CollUtil.isEmpty(opcPoint)) {
            return;
        }

        opcList.forEach(opc -> {
            plcRepository.deletePlcPoint(opc.getId());
            List<OpcPointVo> list = opcPoint.stream().filter(f -> f.getAssetId().equals(opc.getAssetId())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(list)) {
                List<PlcPoint> plcPoints = new ArrayList<>();
                for (OpcPointVo opcPointVo : list) {
                    PlcPoint plcPoint = new PlcPoint();
                    plcPoint.setId(CommonUtil.getId());
                    plcPoint.setPkPlc(opc.getId());
                    plcPoint.setEndpointUrl(opcPointVo.getEndPointUrl());
                    plcPoint.setNamespaceIndex(opcPointVo.getNamespaceIndex());
                    plcPoint.setIdentifier(opcPointVo.getIdentifier());
                    plcPoint.setPointType(opcPointVo.getMsgType());
                    plcPoint.setSort(opcPointVo.getSort());
                    plcPoints.add(plcPoint);
                }

                plcRepository.insertPlcPoint(plcPoints);
            }
        });

    }


}
