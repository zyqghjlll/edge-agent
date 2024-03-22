package com.edge.agent.core.relation.allocation;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyq
 */
@Getter
public class Resources<T> {
    private List<T> resourceInfo;
    /**
     * 已分配
     */
    private List<T> allocatedResources;
    /**
     * 可分配
     */
    private List<T> distributableResources;

    public Resources(List<T> resourceInfo) {
        this.resourceInfo = resourceInfo;
        this.allocatedResources = new ArrayList<>();
        if (CollUtil.isNotEmpty(resourceInfo)) {
            this.distributableResources = resourceInfo;
        } else {
            this.distributableResources = new ArrayList<>();
        }
    }

    public List<T> extract(int count) {
        List<T> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(this.distributableResources)) {
            int resourceCount = this.distributableResources.size();
            for (int i = 0; i < Math.min(resourceCount, count); i++) {
                T t = this.distributableResources.get(0);
                result.add(t);
                this.distributableResources.remove(t);
                this.allocatedResources.add(t);
            }
        }
        return result;
    }
}
