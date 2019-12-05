package me.phoboslabs.illuminati.processor.infra.backup;

import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;

import java.util.List;
import java.util.Map;

public interface Backup<T> {

    void appendByJsonString (IlluminatiInterfaceType illuminatiInterfaceType, String jsonStringData);

    List<T> getDataByList (boolean isPaging, boolean isAfterDelete, int from, int size) throws Exception;

    Map<Integer, T> getDataByMap (boolean isPaging, boolean isAfterDelete, int from, int size) throws Exception;

    void deleteById (int id);

    int getCount () throws Exception;
}
