/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.processor.exception.IlluminatiQueueException;
import org.apache.commons.collections.CollectionUtils;

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
            throw new IlluminatiQueueException("Queue is empty.");
        }

        if (superQueueSize > this.listCount) {
            superQueueSize = this.listCount;
        }

        List<E> dataList = new ArrayList<>(superQueueSize);

        for (int i = 0; i < superQueueSize; i++) {
            try {
                E data = super.poll(timeout, unit);
                if (data == null) {
                    break;
                }
                dataList.add(data);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

        if (CollectionUtils.isEmpty(dataList)) {
            throw new IlluminatiQueueException("Queue is empty.");
        }

        return dataList;
    }
}
