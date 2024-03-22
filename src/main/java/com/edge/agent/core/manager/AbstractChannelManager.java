package com.edge.agent.core.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.netpoint.NetPoint;
import com.edge.agent.utils.CommonUtil;
import com.edge.agent.utils.SysLogger;
import com.edge.agent.utils.ThreadPoolUtil;
import com.edge.agent.core.channel.Channel;
import com.edge.agent.core.channel.exception.ConnectorException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * @author zyq
 */
public abstract class AbstractChannelManager<U, D> {
    /**
     * 管道池
     */
    private ConcurrentHashMap<ChannelReceiveRunner, Channel<U, D>> channelPool = new ConcurrentHashMap<>();
    /**
     * 需要建立管道的网络位置
     */
    private CopyOnWriteArrayList<ChannelConfig> channelConfigs;

    private ExecutorService executor;

    public AbstractChannelManager() {
        this.channelConfigs = new CopyOnWriteArrayList<>();
        executor = ThreadPoolUtil.initThreadPool(30);
    }

    public List<ChannelConfig> channelConfigs() {
        List<ChannelConfig> clone = new ArrayList<>(channelConfigs);
        Collections.copy(clone, channelConfigs);
        return clone;
    }

    public int channelCount() {
        return channelPool.size();
    }

    /**
     * 根据管道配置创建管道
     *
     * @param config
     * @return
     */
    private Channel<U, D> createChannel(ChannelConfig<U, D> config) {
        return new Channel<>(config.getConnector(), config.getReceiveListener(), config.getSendListener(), config.getStatusListener());
    }

    /**
     * 创建管道线程
     */
    private void createThread(ChannelConfig config) {
        Channel channel = createChannel(config);
        ChannelReceiveRunner channelReceiveRunner = new ChannelReceiveRunner(channel, config.getMsgCycle());
        Thread thread = new Thread(channelReceiveRunner);
        thread.setName("Receive-thread-" + channel.getConnector().getRemote().getIp());
        thread.setDaemon(true);
        thread.start();
        channelPool.put(channelReceiveRunner, channel);
    }

    /**
     * 管道维保
     */
    public abstract void channelStatusChanged(Channel<U, D> channel);

    protected void maintainChannel() {
        // 通信管道健康状态巡检
        // 1、使用runAsync或supplyAsync发起异步调用
        List<CompletableFuture<String>> futures = new ArrayList<>();

        //记录开始时间
        Long start = System.currentTimeMillis();

        for (ChannelConfig config : channelConfigs) {
            CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    Channel<U, D> matchChannel = findChannel(config.getConnector().getRemote());
                    if (ObjectUtil.isEmpty(matchChannel)) {
                        createThread(config);
                    } else {
                        channelStatusChanged(matchChannel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SysLogger.error(e, String.format("supplyAsync 执行线程：[%s]，处理通信管道健康状态巡检时出错， ChannelConfig:[%s]", Thread.currentThread().getName(), config.toString()));
                }
                return "";
            }, executor);
            futures.add(cf);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .whenComplete((v, th) -> {
                    SysLogger.info("所有通信管道健康状态巡检执行完成，耗时=" + (System.currentTimeMillis() - start));
                }).join();
    }

    /**
     * 匹配管道
     *
     * @param netPoint
     * @return
     */
    public Channel<U, D> findChannel(NetPoint netPoint) {
        Channel<U, D> channel = null;
        Optional<Map.Entry<ChannelReceiveRunner, Channel<U, D>>> any = channelPool.entrySet()
                .stream()
                .filter(kvEntry -> kvEntry.getValue().contact().getRemote().same(netPoint))
                .findAny();
        if (any.isPresent()) {
            channel = any.get().getValue();
        }
        return channel;
    }

    /**
     * 匹配管道
     *
     * @param netPoint
     * @return
     */
    public ChannelConfig findConfig(NetPoint netPoint) {
        ChannelConfig channelConfig = null;
        Optional<ChannelConfig> any = channelConfigs
                .stream()
                .filter(kvEntry -> kvEntry.getConnector().getRemote().same(netPoint))
                .findAny();
        if (any.isPresent()) {
            channelConfig = any.get();
        }
        return channelConfig;
    }

    /**
     * 获取管道线程
     *
     * @param netPoint
     * @return
     */
    private ChannelReceiveRunner findRunner(NetPoint netPoint) {
        ChannelReceiveRunner result = null;
        for (Map.Entry<ChannelReceiveRunner, Channel<U, D>> entry : channelPool.entrySet()) {
            if (entry.getValue().contact().getRemote().same(netPoint)) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    /**
     * 新增网络位置
     *
     * @param config
     */
    public synchronized void add(ChannelConfig config) {
        Optional<ChannelConfig> any = channelConfigs.stream().filter(f -> f.getConnector().getRemote().same(config.getConnector().getRemote())).findAny();
        if (!any.isPresent()) {
            channelConfigs.add(config);
        }
    }

    /**
     * 新增网络位置
     *
     * @param configs
     */
    public synchronized void add(List<ChannelConfig> configs) {
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        configs.forEach(this::add);
    }

    /**
     * 移除网络位置
     *
     * @param config
     */
    public synchronized void remove(ChannelConfig config) {
        ChannelReceiveRunner runner = findRunner(config.getConnector().getRemote());
        if (null != runner) {
            runner.stop();
            Channel channel = channelPool.get(runner);
            if (ObjectUtil.isNotEmpty(channel)) {
                try {
                    channel.disconnect();
                    channelPool.remove(runner);
                    channelConfigs.remove(config);
                } catch (ConnectorException e) {
                    SysLogger.error(e, "channel disconnect error. channel:[%s]", channel.toString());
                }
            }
        }
    }

    /**
     * 移除网络位置
     *
     * @param configs
     */
    public synchronized void remove(List<ChannelConfig> configs) {
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        configs.forEach(this::remove);
    }

    /**
     * 移除网络位置
     */
    public synchronized void removeAll() {
        if (CollUtil.isNotEmpty(this.channelConfigs)) {
            List<ChannelConfig> configsClone = channelConfigs();
            for (ChannelConfig item : configsClone) {
                remove(item);
            }
        }
    }

    /**
     * 获取管道监控信息
     *
     * @return
     */
    public List<Monitoring> monitoring() {
        List<Monitoring> result = new ArrayList<>();
        for (Map.Entry<ChannelReceiveRunner, Channel<U, D>> entry : channelPool.entrySet()) {
            Channel channel = entry.getValue();
            Monitoring monitoring = new Monitoring();
            monitoring.setPkRelation(findConfig(channel.contact().getRemote()).getPkRelation());
            monitoring.setServer(channel.getConnector().getServer());
            monitoring.setLocal(channel.contact().getLocal());
            monitoring.setRemote(channel.contact().getRemote());
            monitoring.setStatus(channel.status().getCode());
            monitoring.setReason(channel.errorMessage());
            monitoring.setUptime(CommonUtil.getDate());

            result.add(monitoring);
        }
        return result;
    }
}
