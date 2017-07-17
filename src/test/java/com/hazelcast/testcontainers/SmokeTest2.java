package com.hazelcast.testcontainers;

import com.hazelcast.core.IMap;
import org.junit.ClassRule;
import org.junit.Test;
import support.HazelcastTestApp;

import static com.hazelcast.testcontainers.HazelcastContainer.HAZELCAST_DOCKER_IMAGE_NAME;
import static org.hamcrest.CoreMatchers.is;

/**
 * TODO
 *
 * @author Viktor Gamov on 7/17/17.
 * Twitter: @gamussa
 * @since 0.0.1
 */
public class SmokeTest2 {
    @ClassRule
    public static HazelcastTestApp app = new HazelcastTestApp(HAZELCAST_DOCKER_IMAGE_NAME, "latest", "hazelcast.xml", "hazelcast-client.xml");

    @Test
    public void simpleTest() {
        final IMap<Integer, String> test = app.getHazelcastClient().getMap("test");
        test.put(1, "test");
        is(test.get(1)).matches("test");
    }
}
