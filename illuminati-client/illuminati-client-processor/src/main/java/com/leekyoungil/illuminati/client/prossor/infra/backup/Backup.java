package com.leekyoungil.illuminati.client.prossor.infra.backup;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutorType;

import java.util.List;
import java.util.Map;

public interface Backup<T> {

    void append (IlluminatiExecutorType illuminatiExecutorType, T data);

    List<T> getDataByList (boolean isPaging, boolean isAfterDelete, int from, int size);

    Map<Integer, T> getDataByMap (boolean isPaging, boolean isAfterDelete, int from, int size);

    void deleteById (int id);

    int getCount ();
}
