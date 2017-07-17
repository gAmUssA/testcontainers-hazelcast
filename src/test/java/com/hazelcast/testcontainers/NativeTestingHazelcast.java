package com.hazelcast.testcontainers;

import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NativeTestingHazelcast {

    private TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance member;
    private HazelcastInstance client;

    @Before
    public void setUp() throws Exception {
        member = hazelcastFactory.newHazelcastInstance();
        client = hazelcastFactory.newHazelcastClient();
    }

    @Test
    public void simpleTest() throws Exception {
        final IMap<Integer, String> testMapFromMember = member.getMap("testMap");
        testMapFromMember.set(1, "test1");

        final IMap<Integer, String> testMap = client.getMap("testMap");
        final String value = testMap.get(1);
        assertEquals("member puts, client gets", value, "test1");
    }

    @After
    public void tearDown() throws Exception {
        hazelcastFactory.shutdownAll();
    }
}
