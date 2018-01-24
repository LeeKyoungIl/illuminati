package com.leekyoungil.illuminati.client.prossor.infra.backup.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutorType;
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup;

import java.util.List;

public class H2Backup<T> implements Backup<T> {

    @Override public void append(IlluminatiExecutorType illuminatiExecutorType, T data) {

    }

    @Override public List<T> getDataList() {
        return null;
    }
}
