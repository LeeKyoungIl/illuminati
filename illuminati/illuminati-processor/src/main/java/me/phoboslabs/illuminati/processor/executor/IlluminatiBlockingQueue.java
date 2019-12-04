package me.phoboslabs.illuminati.processor.executor;

import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
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

    public List<E> pollToList(long timeout, TimeUnit unit) throws Exception {
        int superQueueSize = super.size();
        if (superQueueSize == 0) {
            throw new Exception("Queue is empty.");
        }

        if (superQueueSize > this.listCount) {
            superQueueSize = this.listCount;
        }

        List<E> dataList = new ArrayList<E>(superQueueSize);

        for (int i=0; i<superQueueSize; i++) {
            try {
                E data = super.poll(timeout, unit);
                if (data == null) {
                    break;
                }
                dataList.add(data);
            } catch (InterruptedException ignore) {}
        }

        if (CollectionUtils.isEmpty(dataList)) {
            throw new Exception("Queue is empty.");
        }

        return dataList;
    }
}
