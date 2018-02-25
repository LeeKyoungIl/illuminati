package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class IlluminatiBlockingQueue<E extends IlluminatiInterfaceModel> extends LinkedBlockingQueue<E> {

    private int listCount = 1;

    public IlluminatiBlockingQueue(int capacity, int listCount) {
        super(capacity);
        this.listCount = listCount;
    }

    public List<E> pollToList(long timeout, TimeUnit unit) {
        List<E> dataList = new ArrayList<E>(this.listCount);

        for (int i=0; i<this.listCount; i++) {
            try {
                E data = super.poll(timeout, unit);
                if (data != null) {
                    dataList.add(data);
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }

        return CollectionUtils.isEmpty(dataList) == true ? null : dataList;
    }
}
