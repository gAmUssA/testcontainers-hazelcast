package com.hazelcast.testcontainers;

import com.hazelcast.core.IMap;
import org.junit.ClassRule;
import org.junit.Test;
import support.HazelcastTestApp;

import static org.hamcrest.CoreMatchers.is;

/**
 * TODO
 *
 * @author Viktor Gamov on 5/2/17.
 * Twitter: @gamussa
 * @since 0.0.1
 */
public class SmokeTest {
    @ClassRule
    public static HazelcastTestApp app = new HazelcastTestApp();

    @Test
    public void simpleTest() {
        final IMap<Integer, String> test = app.getHazelcastClient().getMap("test");
        test.put(1, "test");
        is(test.get(1)).matches("test");
    }
}
