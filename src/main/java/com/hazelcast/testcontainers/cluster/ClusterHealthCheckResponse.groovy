package com.hazelcast.testcontainers.cluster

import groovy.transform.Canonical
import groovy.transform.CompileStatic

/**
 *  TODO
 *
 * @author Viktor Gamov on 5/7/17.
 *  Twitter: @gamussa
 * @since 0.0.1
 */
@Canonical
@CompileStatic
class ClusterHealthCheckResponse {
    String nodeState
    String clusterState
    boolean clusterSafe
    int migrationQueueSize
    int clusterSize

    /**
     Example of the response:

     Hazelcast::NodeState=ACTIVE
     Hazelcast::ClusterState=ACTIVE
     Hazelcast::ClusterSafe=TRUE
     Hazelcast::MigrationQueueSize=0
     Hazelcast::ClusterSize=1
     */
    ClusterHealthCheckResponse(String[] split) {
        nodeState = split[0].split("::")[1].split("=")[1]
        clusterState = split[1].split("::")[1].split("=")[1]
        clusterSafe = split[2].split("::")[1].split("=")[1].toLowerCase() as boolean
        migrationQueueSize = split[3].split("::")[1].split("=")[1].toInteger()
        clusterSize = split[4].split("::")[1].split("=")[1].toInteger()
    }
}
