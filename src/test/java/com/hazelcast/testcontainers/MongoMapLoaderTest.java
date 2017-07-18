package com.hazelcast.testcontainers;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.loader.Supplement;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.Wait;

import static com.hazelcast.testcontainers.HazelcastContainer.createHazelcastOSSContainerWithConfigFile;
import static com.hazelcast.testcontainers.MongoMapLoaderTest.MongoContainer.MONGO_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test for Hazelcast mapLoader with Mongo (in containers)
 *
 * @author Viktor Gamov on 7/17/17.
 * Twitter: @gamussa
 * @since 0.0.1
 */
@Slf4j
public class MongoMapLoaderTest {

    @ClassRule
    public static Network network = Network.newNetwork();

    @ClassRule
    public static MongoContainer mongo =
            new MongoContainer("mongo:3.1.5")
                    .withExposedPorts(MONGO_PORT)
                    .withLogConsumer(new Slf4jLogConsumer(log))
                    .withNetwork(network)
                    .withNetworkAliases("mongo")
                    .waitingFor(Wait.forListeningPort());
    @ClassRule
    public static HazelcastContainer hazelcast =
            createHazelcastOSSContainerWithConfigFile("latest", "hazelcast-loaderTest.xml")
                    .withLogConsumer(new Slf4jLogConsumer(log))
                    .withNetwork(network)
                    .waitingFor(Wait.forListeningPort())
                    .withExposedPorts(5701)
                    .withClasspathResourceMapping("fatHazelcast.jar", "/opt/hazelcast/lib/fatHazelcast.jar", BindMode.READ_ONLY)
                    .withEnv("CLASSPATH", "/opt/hazelcast/lib")
                    .withEnv("JAVA_OPTS", "-Dmongo.url=mongodb://mongo:" + MONGO_PORT);

    private static HazelcastInstance hazelcastClient;
    private static IMap<String, Supplement> supplements;

    @BeforeClass
    public static void initClient() throws Exception {
        ClientConfig cc = new XmlClientConfigBuilder("hazelcast-client.xml").build();
        cc.getNetworkConfig().addAddress(hazelcast.getContainerIpAddress() + ":" + hazelcast.getFirstMappedPort());
        hazelcastClient = HazelcastClient.newHazelcastClient(cc);
        supplements = hazelcastClient.getMap("supplements");
    }


    @Test
    public void shouldLoadFromMongo() throws Exception {
        supplements.set("1", new Supplement("bcaa", 10));
        supplements.set("2", new Supplement("protein", 100));
        supplements.set("3", new Supplement("glucosamine", 200));

        assertThat(supplements.size()).isNotNull().isEqualTo(3);
    }

    @Test
    public void shouldBeEmptyAfterEvict() throws Exception {
        supplements.evictAll();
        assertThat(supplements.size()).isNotNull().isEqualTo(0);
    }

    @Test
    public void shouldReloadFromStoreWithLoadAll() throws Exception {
        supplements.loadAll(true);
        assertThat(supplements.size()).isNotNull().isEqualTo(3);
    }

    static class MongoContainer extends GenericContainer<MongoContainer> {
        public static final int MONGO_PORT = 27017;

        public MongoContainer(String dockerImageName) {
            super(dockerImageName);
        }

        @Override
        protected void configure() {
            super.configure();

            withExposedPorts(MONGO_PORT);
            waitingFor(Wait.forListeningPort());
        }

        public String getMongoUrl() {
            return String.format("mongodb://%s:%d", this.getContainerIpAddress(), this.getFirstMappedPort());
        }
    }
}
