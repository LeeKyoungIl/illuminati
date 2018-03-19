package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IlluminatiBlockingQueue<E extends IlluminatiInterfaceModel> extends LinkedBlockingQueue<E> {

    private int listCount = 1;
    private final Lock takeListLock = new ReentrantLock();
    private final Condition notListEmpty = takeListLock.newCondition();

    public IlluminatiBlockingQueue(int capacity, int listCount) {
        super(capacity);
        this.listCount = listCount;
    }

    public List<E> pollToList(long timeout, TimeUnit unit) throws InterruptedException {
        int superQueueSize = 0;
        final Lock takeListLock = this.takeListLock;
        takeListLock.lockInterruptibly();
        try {
            while (super.size() < this.listCount) {
                notListEmpty.await();
            }
            notListEmpty.signal();
            superQueueSize = super.size();
        } finally {
            takeListLock.unlock();
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
