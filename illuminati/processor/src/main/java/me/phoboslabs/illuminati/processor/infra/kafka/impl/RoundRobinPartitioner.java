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

package me.phoboslabs.illuminati.processor.infra.kafka.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public class RoundRobinPartitioner implements Partitioner {

    private final Random random = SecureRandom.getInstanceStrong();
    private final AtomicInteger counter;

    public RoundRobinPartitioner() throws NoSuchAlgorithmException {
        this.counter = new AtomicInteger((this.random.nextInt(Integer.MAX_VALUE)));
    }

    @Override
    public int partition(String topic, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        final int availablePartitions = cluster.partitionCountForTopic(topic);
        final int partition = counter.getAndIncrement() % availablePartitions;

        if (counter.get() == Integer.MAX_VALUE) {
            counter.set(0);
        }

        return partition;
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
    }
}
