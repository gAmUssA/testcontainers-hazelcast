package com.hazelcast.testcontainers;

import com.hazelcast.spi.properties.HazelcastProperty;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.util.HashSet;
import java.util.Set;

import static com.hazelcast.spi.properties.GroupProperty.HTTP_HEALTHCHECK_ENABLED;
import static com.hazelcast.spi.properties.GroupProperty.REST_ENABLED;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * Hazelcast Docker container
 *
 * @author Viktor Gamov on 5/2/17.
 * Twitter: @gamussa
 * @since 0.0.1
 */
public class HazelcastContainer extends GenericContainer<HazelcastContainer> {

    private static final String IMAGE_AND_VERSION_FORMAT = "%s:%s";
    public static String HAZELCAST_DOCKER_IMAGE_NAME = "hazelcast/hazelcast";
    public static String HAZELCAST_ENTERPRISE_DOCKER_IMAGE_NAME = "hazelcast/hazelcast-enterprise";

    private Set<HazelcastProperty> enabledFeatures = new HashSet<>();
    private Set<String> customProperties = new HashSet<>();

    protected HazelcastContainer(String dockerImageName) {
        super(dockerImageName);
    }

    protected HazelcastContainer(String dockerImageName, String versionTag) {
        super(format(IMAGE_AND_VERSION_FORMAT, dockerImageName, versionTag));
    }

    static HazelcastContainer createHazelcastOSSContainer(String versionTag) {
        return new HazelcastContainer(format(IMAGE_AND_VERSION_FORMAT, HAZELCAST_DOCKER_IMAGE_NAME, versionTag));
    }

    static HazelcastContainer createHazelcastOSSContainer() {
        return new HazelcastContainer(format(IMAGE_AND_VERSION_FORMAT, HAZELCAST_DOCKER_IMAGE_NAME, "latest"));
    }

    static HazelcastContainer createHazelcastOSSContainerWithConfigFile(String configFile) {
        return createHazelcastOSSContainerWithConfigFile("latest", configFile);
    }

    /**
     * Creates container with version and hazelcast.xml config
     *
     * @param versionTag version tag
     * @param configFile hazelcast.xml config file from classpath
     * @return HazelcastContainer configured with version and config xml
     */
    static HazelcastContainer createHazelcastOSSContainerWithConfigFile(String versionTag, String configFile) {
        return new
                HazelcastContainer(format(IMAGE_AND_VERSION_FORMAT, HAZELCAST_DOCKER_IMAGE_NAME, versionTag))
                .withClasspathResourceMapping(configFile, "/opt/hazelcast/hazelcast.xml", BindMode.READ_ONLY);
    }

    HazelcastContainer withHTTPHealthCheck() {
        enabledFeatures.add(HTTP_HEALTHCHECK_ENABLED);
        return this;
    }

    HazelcastContainer withRESTClient() {
        enabledFeatures.add(REST_ENABLED);
        return this;
    }

    HazelcastContainer withCustomProperty(String property) {
        customProperties.add(property);
        return this;
    }

    @Override
    protected void configure() {
        super.configure();

        final String javaOpts =
                enabledFeatures
                        .stream().map(prop -> format("-D%s=true ", prop.getName())).collect(joining(" "));
        final String customProps =
                customProperties.stream().map(p -> format("-D%s", p)).collect(joining(" "));

        logger().debug(javaOpts);
        withEnv("JAVA_OPTS", javaOpts.concat(" " + customProps));

    }

    public String getRestBaseUrl() {
        final String containerIpAddress = this.getContainerIpAddress();
        final Integer firstMappedPort = this.getFirstMappedPort();
        return format("http://%s:%d/hazelcast/rest", containerIpAddress, firstMappedPort);
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
