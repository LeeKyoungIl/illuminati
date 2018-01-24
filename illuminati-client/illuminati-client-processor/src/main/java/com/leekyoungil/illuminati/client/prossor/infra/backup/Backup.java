package com.leekyoungil.illuminati.client.prossor.infra.backup;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutorType;

import java.util.List;

public interface Backup<T> {

    void append (IlluminatiExecutorType illuminatiExecutorType, T data);

    List<T> getDataList ();
}
