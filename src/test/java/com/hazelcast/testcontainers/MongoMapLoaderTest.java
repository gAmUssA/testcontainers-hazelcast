package com.hazelcast.testcontainers;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.loader.Supplement;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.Wait;

import static com.hazelcast.testcontainers.HazelcastContainer.createHazelcastOSSContainerWithConfigFile;

/**
 * A test for Hazelcast mapLoader with Mongo (in containers)
 *
 * @author Viktor Gamov on 7/17/17.
 * Twitter: @gamussa
 * @since 0.0.1
 */
@Slf4j
public class MongoMapLoaderTest {

    private static final int MONGO_PORT = 27017;

    @ClassRule
    public static Network network = Network.newNetwork();

    public static GenericContainer mongo = new GenericContainer("mongo:3.1.5")
            .withLogConsumer(new Slf4jLogConsumer(log))
            .withNetwork(network)
            .waitingFor(Wait.forListeningPort())
            .withExposedPorts(MONGO_PORT);

    public static HazelcastContainer hazelcast =
            createHazelcastOSSContainerWithConfigFile("latest", "hazelcast-loaderTest.xml")
                    .withLinkToContainer(mongo, "mongo")
                    .withLogConsumer(new Slf4jLogConsumer(log))
                    .withNetwork(network)
                    .waitingFor(Wait.forListeningPort())
                    .withExposedPorts(5701)
                    .withClasspathResourceMapping("fatHazelcast.jar", "/opt/hazelcast/lib/fatHazelcast.jar", BindMode.READ_ONLY)
                    .withEnv("CLASSPATH", "/opt/hazelcast/lib");
                    //.withEnv("mongo.url", "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getFirstMappedPort());

    @ClassRule
    public static RuleChain r = RuleChain.outerRule(network).around(mongo).around(hazelcast);

    @Test
    public void testIt() throws Exception {
        ClientConfig cc = new XmlClientConfigBuilder("hazelcast-client.xml").build();
        cc.getNetworkConfig().addAddress(hazelcast.getContainerIpAddress() + ":" + hazelcast.getFirstMappedPort());
        final HazelcastInstance hazelcastClient = HazelcastClient.newHazelcastClient(cc);

        IMap<String, Supplement> supplements = hazelcastClient.getMap("supplements");
        System.out.println(supplements.size());

        supplements.set("1", new Supplement("bcaa", 10));
        supplements.set("2", new Supplement("protein", 100));
        supplements.set("3", new Supplement("glucosamine", 200));

        System.out.println(supplements.size());

        supplements.evictAll();

        System.out.println(supplements.size());

        supplements.loadAll(true);

        System.out.println(supplements.size());

    }
}
