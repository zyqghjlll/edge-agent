package com.edge.agent.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ContextData<K, V> {

    private HashMap<K, V> hashMap = new HashMap<>();

    public ContextData() {
        init();
    }

    private void init() {
        reload();
    }

    protected abstract HashMap<K, V> load();

    public abstract V load(K k);

    public synchronized V get(K k) {
        V v = null;
        Optional<Map.Entry<K, V>> any = hashMap.entrySet().stream().filter(f -> f.getKey().equals(k)).findAny();
        if (any.isPresent()) {
            v = any.get().getValue();
        } else {
            v = load(k);
            if (ObjectUtil.isNotEmpty(v)) {
                this.hashMap.put(k, v);
            }
        }
        return v;
    }

    public void put(K k, V v) {
        this.hashMap.put(k, v);
    }

    public HashMap<K, V> getAll() {
        return new HashMap<>(this.hashMap);
    }

    public void remove(K k) {
        this.hashMap.remove(k);
    }

    public void reload() {
        HashMap<K, V> dataMap = load();
        if (CollUtil.isNotEmpty(dataMap)) {
            this.hashMap = dataMap;
        }
    }
}
