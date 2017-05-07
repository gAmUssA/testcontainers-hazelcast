package com.hazelcast.testcontainers;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static com.hazelcast.testcontainers.HazelcastContainer.createHazelcastOSSContainer;
import static org.hamcrest.CoreMatchers.is;

/**
 * TODO
 *
 * @author Viktor Gamov on 5/2/17.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
public class SmokeTest {
    @ClassRule
    public static HazelcastContainer hazelcast =
            createHazelcastOSSContainer()
                    .withExposedPorts(5701);

    private HazelcastInstance hazelcastClient;

    @Before
    public void setUp() throws Exception {
        ClientConfig cc = new ClientConfig();
        final String containerIpAddress = hazelcast.getContainerIpAddress();
        cc.getNetworkConfig().addAddress(containerIpAddress + ":" + hazelcast.getMappedPort(5701));
        hazelcastClient = HazelcastClient.newHazelcastClient(cc);
    }

    @Test
    public void simpleTest() {

        final IMap<Integer, String> test = hazelcastClient.getMap("test");
        test.put(1, "test");
        is(test.get(1)).matches("test");

    }
}
