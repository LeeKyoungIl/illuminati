package me.phoboslabs.illuminati.prossor.infra.kafka.impl;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public class RoundRobinPartitioner implements Partitioner {

    private final AtomicInteger counter = new AtomicInteger((new Random().nextInt(Integer.MAX_VALUE)));

    @Override
    public int partition (String topic, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        final int availablePartitions = cluster.partitionCountForTopic(topic);
        final int partition = counter.getAndIncrement() % availablePartitions;

        if (counter.get() == Integer.MAX_VALUE) {
            counter.set(0);
        }

        return partition;
    }

    @Override
    public void close() { }

    @Override
    public void configure(Map<String, ?> map) { }
}
