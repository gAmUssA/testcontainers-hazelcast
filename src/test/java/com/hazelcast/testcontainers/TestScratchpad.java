package com.hazelcast.testcontainers;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.internal.partition.InternalPartitionService;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

/**
 * simple class for trying small things with Hazelcast
 *
 * @author Viktor Gamov on 5/7/17.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
public class TestScratchpad {
    public static void main(String[] args) {
        //System.setProperty("hazelcast.http.healthcheck.enabled", "true");
        final HazelcastInstance hazelcastInstance = newHazelcastInstance();

        HazelcastInstanceImpl hazelcastInstance1 = ((HazelcastInstanceProxy) hazelcastInstance).getOriginal();
        final InternalPartitionService partitionService = hazelcastInstance1.node.getPartitionService();

        boolean memberStateSafe = partitionService.isMemberStateSafe();
        System.out.println("memberStateSafe = " + memberStateSafe);
        boolean clusterSafe = memberStateSafe && !partitionService.hasOnGoingMigration();
        System.out.println("clusterSafe = " + clusterSafe);
        long migrationQueueSize = partitionService.getMigrationQueueSize();
        System.out.println("migrationQueueSize = " + migrationQueueSize);


    }
}

