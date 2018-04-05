package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class IlluminatiBlockingQueue<E extends IlluminatiInterfaceModel> extends LinkedBlockingQueue<E> {

    private int listCount = 1;

    public IlluminatiBlockingQueue(int capacity, int listCount) {
        super(capacity);
        this.listCount = listCount;
    }

    public List<E> pollToList(long timeout, TimeUnit unit) {
        int superQueueSize = super.size();
        if (superQueueSize == 0) {
            return null;
        }

        if (superQueueSize > this.listCount) {
            superQueueSize = this.listCount;
        }

        List<E> dataList = new ArrayList<E>(superQueueSize);

        for (int i=0; i<superQueueSize; i++) {
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
