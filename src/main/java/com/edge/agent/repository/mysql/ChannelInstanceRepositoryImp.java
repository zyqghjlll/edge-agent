package com.edge.agent.repository.mysql;

import cn.hutool.core.collection.CollUtil;
import com.edge.agent.core.agent.Agent;
import com.edge.agent.core.manager.ChannelInstanceRepository;
import com.edge.agent.core.manager.Monitoring;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.core.netpoint.exception.NetPointException;
import com.edge.agent.core.plc.Plc;
import com.edge.agent.core.relation.Relations;
import com.edge.agent.repository.mysql.entity.DbAllocationRelation;
import com.edge.agent.repository.mysql.entity.DbChannelInstance;
import com.edge.agent.repository.mysql.entity.DbPlc;
import com.edge.agent.repository.mysql.mapper.DbAllocationRelationMapper;
import com.edge.agent.repository.mysql.mapper.DbChannelInstanceMapper;
import com.edge.agent.repository.mysql.mapper.DbPlcMapper;
import com.edge.agent.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zyq
 */
@Repository
public class ChannelInstanceRepositoryImp implements ChannelInstanceRepository {

    @Autowired
    private DbPlcMapper dbPlcMapper;
    @Autowired
    private DbAllocationRelationMapper dbAllocationRelationMapper;
    @Autowired
    private DbChannelInstanceMapper dbChannelInstanceMapper;

    @Override
    public List<Plc> findByAgents(List<Agent> agents) {
        List<Plc> result = new ArrayList<>();
        List<Long> pkAgents = agents.stream().map(Agent::getId).collect(Collectors.toList());
        List<DbAllocationRelation> channelManagers = dbAllocationRelationMapper.findByAgents(pkAgents);
        if (CollUtil.isEmpty(channelManagers)) {
            return result;
        }
        List<Long> plcIds = channelManagers.stream().map(DbAllocationRelation::getPkPlc).collect(Collectors.toList());
        List<DbPlc> dbPlcList = dbPlcMapper.selectBatchIds(plcIds);
        dbPlcList.forEach(item -> {
            try {
                Plc plc = item.to();
                result.add(plc);
            } catch (NetPointException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    @Override
    public void addRelation(Agent agent, Collection<Plc> plcs, Long bufferSize) {
        if (CollUtil.isEmpty(plcs)) {
            return;
        }
        plcs.forEach(plcNode -> {
            DbAllocationRelation dbAllocationRelation = new DbAllocationRelation();
            dbAllocationRelation.setPkAllocationRelation(CommonUtil.getId());
            dbAllocationRelation.setPkAgent(agent.getId());
            dbAllocationRelation.setPkPlc(plcNode.getId());
            dbAllocationRelation.setBufferSize(bufferSize);
            dbAllocationRelationMapper.insert(dbAllocationRelation);
        });
    }

    @Override
    public List<Plc> findByPlc(Agent agent, Collection<String> assetIds) {
        List<Plc> result = new ArrayList<>();
        List<DbAllocationRelation> channelManagers = dbAllocationRelationMapper.findByAgent(agent.getId());
        if (CollUtil.isEmpty(channelManagers)) {
            return result;
        }
        List<Long> plcIds = channelManagers.stream().map(DbAllocationRelation::getPkPlc).collect(Collectors.toList());
        List<DbPlc> dbPlcList = dbPlcMapper.selectBatchIds(plcIds);
        dbPlcList.forEach(item -> {
            try {
                Plc plc = item.to();
                if (CollUtil.isEmpty(assetIds)) {
                    result.add(plc);
                } else {
                    if (assetIds.contains(plc.getAssetId())) {
                        result.add(plc);
                    }
                }
            } catch (NetPointException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    @Override
    public List<Plc> findByAgent(Agent agent) {
        List<Agent> agents = new ArrayList<>();
        agents.add(agent);
        return findByAgents(agents);
    }

    @Override
    public List<Relations> getRelations(Long pkAgent) {
        List<Relations> result = new ArrayList<>();
        List<DbAllocationRelation> byAgent = dbAllocationRelationMapper.findByAgent(pkAgent);
        if (CollUtil.isEmpty(byAgent)) {
            return result;
        }
        byAgent.forEach(item -> {
            Relations relations = new Relations(item.getPkAllocationRelation(), item.getPkAgent(), item.getPkPlc());
            result.add(relations);
        });
        return result;
    }

    @Override
    public void uptime(Agent agent, List<Monitoring> monitoring) {
        deleteUn(agent);

        Map<NetPoint, List<Monitoring>> map = monitoring.stream().collect(Collectors.groupingBy(Monitoring::getServer));
        map.forEach((n, l) -> {
            l.forEach(m -> {
                List<DbChannelInstance> byRelation = dbChannelInstanceMapper.findByRelation(m.getPkRelation());
                if (CollUtil.isEmpty(byRelation)) {
                    DbChannelInstance dbChannelInstance = this.makeInstance(CommonUtil.getId(), m);
                    dbChannelInstanceMapper.insert(dbChannelInstance);
                } else {
                    DbChannelInstance dbChannelInstance = this.makeInstance(byRelation.get(0).getPkChannelInstance(), m);
                    dbChannelInstanceMapper.updateById(dbChannelInstance);
                }
            });
        });
    }

    private void deleteUn(Agent agent) {
        List<DbAllocationRelation> relationList = dbAllocationRelationMapper.findByAgent(agent.getId());
        List<DbChannelInstance> instanceList = dbChannelInstanceMapper.findByAgent(agent.getServerIp());
        deleteUnInstance(relationList, instanceList);
    }

    private DbChannelInstance makeInstance(Long pkInstance, Monitoring monitoring) {
        DbChannelInstance dbChannelInstance = new DbChannelInstance();
        dbChannelInstance.setPkChannelInstance(pkInstance);
        dbChannelInstance.setPkAllocationRelation(monitoring.getPkRelation());
        dbChannelInstance.setAgentIp(monitoring.getLocal().getIp());
        dbChannelInstance.setAgentPort(monitoring.getLocal().getPort() + "");
        dbChannelInstance.setPlcIp(monitoring.getRemote().getIp());
        dbChannelInstance.setPlcPort(monitoring.getRemote().getPort() + "");
        dbChannelInstance.setStatus(monitoring.getStatus());
        dbChannelInstance.setReason(monitoring.getReason());
        dbChannelInstance.setUptime(monitoring.getUptime());
        return dbChannelInstance;
    }

    private void deleteUnInstance(List<DbAllocationRelation> relationList, List<DbChannelInstance> instanceList) {
        List<Long> relationIdList = relationList.stream().map(DbAllocationRelation::getPkAllocationRelation).collect(Collectors.toList());
        List<Long> instanceIdList = instanceList.stream().map(DbChannelInstance::getPkAllocationRelation).collect(Collectors.toList());
        List<Long> needDeleteList = instanceIdList.stream().filter(item -> !relationIdList.contains(item)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needDeleteList)) {
            dbChannelInstanceMapper.deleteByRelations(needDeleteList);
        }
    }

    @Override
    public void deleteByPlc(List<Long> plcIds) {
        dbAllocationRelationMapper.deleteByPlc(plcIds);
    }

    @Override
    public void deleteByRelationIds(List<Long> relationIds) {
        dbChannelInstanceMapper.deleteByRelations(relationIds);
    }

    @Override
    public void syncInfo(Plc plc, String message) {
        dbChannelInstanceMapper.updateSyncInfo(plc.getIp(), plc.getPort(), message);
    }
}
