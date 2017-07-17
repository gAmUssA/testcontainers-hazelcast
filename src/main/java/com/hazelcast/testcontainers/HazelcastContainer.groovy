package com.hazelcast.testcontainers

import groovy.transform.CompileStatic
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer

/**
 * Hazelcast Docker container 
 *
 * @author Viktor Gamov on 5/2/17.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
@CompileStatic
class HazelcastContainer extends GenericContainer<HazelcastContainer> {

    public static String HAZELCAST_DOCKER_IMAGE_NAME = "hazelcast/hazelcast"
    public static String HAZELCAST_ENTERPRISE_DOCKER_IMAGE_NAME = "hazelcast/hazelcast-enterprise"

    protected HazelcastContainer(String dockerImageName) {
        super("$dockerImageName:latest")
    }

    protected HazelcastContainer(String dockerImageName, String versionTag) {
        super("$dockerImageName:$versionTag")
    }

    static HazelcastContainer createHazelcastOSSContainer(String versionTag) {
        return new HazelcastContainer("$HAZELCAST_DOCKER_IMAGE_NAME:$versionTag")
    }

    static HazelcastContainer createHazelcastOSSContainer() {
        return new HazelcastContainer("$HAZELCAST_DOCKER_IMAGE_NAME:latest")
    }

    static HazelcastContainer createHazelcastOSSContainerWithConfigFile(String configFile) {
        return createHazelcastOSSContainerWithConfigFile("latest", configFile)
    }

    static HazelcastContainer createHazelcastOSSContainerWithConfigFile(String versionTag, String configFile) {
        return new HazelcastContainer("$HAZELCAST_DOCKER_IMAGE_NAME:$versionTag").withClasspathResourceMapping(configFile, "/opt/hazelcast/$configFile", BindMode.READ_ONLY)
    }

    /*static HazelcastContainer createHazelcastOSSContainer() {
        return createContainer(false)
    }

    static HazelcastContainer createHazelcastEnterpriseContainer() {
        return createContainer(true)
    }

    static HazelcastContainer createContainer(boolean isEnterprise) {
        return isEnterprise ? new HazelcastContainer(HAZELCAST_ENTERPRISE_DOCKER_IMAGE_NAME) : new HazelcastContainer(HAZELCAST_DOCKER_IMAGE_NAME)
    }*/

}
