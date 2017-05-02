package com.hazelcast.testcontainers;

import org.testcontainers.containers.GenericContainer;

/**
 * TODO
 *
 * @author Viktor Gamov on 5/2/17.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
public class HazelcastContainer extends GenericContainer<HazelcastContainer> {
    public static String HAZELCAST_DOCKER_IMAGE_NAME = "hazelcast/hazelcast:latest";
    public static String HAZELCAST_ENTERPRISE_DOCKER_IMAGE_NAME = "hazelcast/hazelcast-enterprise:latest";

    public HazelcastContainer() {
        super(HAZELCAST_DOCKER_IMAGE_NAME);
    }

    public HazelcastContainer(String dockerImageName) {
        super(dockerImageName);
    }

    public static HazelcastContainer createContainer(boolean isEnterprise) {
        return isEnterprise ? new HazelcastContainer(HAZELCAST_ENTERPRISE_DOCKER_IMAGE_NAME) : new HazelcastContainer(HAZELCAST_DOCKER_IMAGE_NAME);
    }

}
